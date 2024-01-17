package dps.truck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.management.RuntimeErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import dps.Message;
import dps.Utils;
import dps.platoon.Follower;
import dps.platoon.Platoon;
import dps.platoon.PrimeFollower;

public class TruckServer extends Thread {
    final static int MAX_RETRIES = 3;
    final static int WAIT_BEFORE_TRY_SECONDS = 2;
    private Logger logger;
    private SocketAddress socketAddress;
    private Truck truck;
    LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    AtomicInteger messageCounter = new AtomicInteger(0);
    private ArrayList<UnacknowledgedMessage> unacknowledgedSentMessages = new ArrayList<>();

    class UnacknowledgedMessage {
        LocalDateTime lastTry;
        Message message;
        int tries = 1;
        SocketAddress receiver;
        ArrayList<Integer> correspondingIdsList = new ArrayList<>();

        public UnacknowledgedMessage(Message message, LocalDateTime lastTry) {
            this.message = message;
            this.lastTry = lastTry;
            this.receiver = SocketAddress.fromString(message.getBody().get("receiver"));
            this.correspondingIdsList.add(message.getId());
        }

        @Override
        public String toString() {
            return String.format("Messages(ids=%s, tries=%d, receiver=%s), lastly at %s",
                    this.correspondingIdsList.toString(),
                    this.tries,
                    this.receiver.toString(),
                    this.lastTry.toString());
        }

        public Message getMessage() {
            return message;
        }

        public int getTries() {
            return tries;
        }

        public void incrementTries() {
            this.tries++;
        }

        public LocalDateTime getLastTry() {
            return lastTry;
        }

        public void setLastTry(LocalDateTime lastTry) {
            this.lastTry = lastTry;
        }

        public ArrayList<Integer> getCorrespondingIdsList() {
            return correspondingIdsList;
        }

        public void addCorrespondingId(int id) {
            this.correspondingIdsList.add(id);
        }
    }

    public TruckServer(SocketAddress socketAddress) throws IOException {
        this.logger = Logger.getLogger(this.getClass().getSimpleName());
        this.socketAddress = socketAddress;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
        truck.start();
    }

    public Truck getTruck() {
        return truck;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(this.socketAddress.getPort())) {
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String line;

                    StringBuilder rStringBuilder = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        rStringBuilder.append(line);
                    }
                    Message receivedMessage = Message.fromJson(rStringBuilder.toString());

                    // Check to see if received message acknowledges any sent messages
                    // If it does, remove it from our unacknowledged messages
                    if (receivedMessage.getBody().get("ack_id") != null){
                        int ackId = Integer.valueOf(receivedMessage.getBody().get("ack_id"));
                        for (UnacknowledgedMessage messageInfo : unacknowledgedSentMessages) {
                            if (messageInfo.getCorrespondingIdsList().contains(ackId)) {
                                unacknowledgedSentMessages.remove(messageInfo);
                                this.logger.fine(messageInfo.toString() + "was acknowledged.");
                                break;
                            }
                        }
                    }

                    this.addMessageToQueue(receivedMessage);

                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error in communication with the client: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!this.truck.isAlive()) {
                    return;
                }

                // Check if messages from before have been acknowledged
                for (UnacknowledgedMessage messageInfo : unacknowledgedSentMessages) {
                    Message message = messageInfo.message;
                    int messageTries = messageInfo.tries;
                    SocketAddress receiver = messageInfo.receiver;
                    if (messageTries == MAX_RETRIES) {
                        this.logger.info("Message has not been acknowledged after 3 tries. Alerting truck.");
                        this.truck.handleUnresponsiveReceiver(message);
                    }
                    if (messageTries < MAX_RETRIES && ChronoUnit.SECONDS.between(messageInfo.lastTry,
                            LocalDateTime.now()) >= WAIT_BEFORE_TRY_SECONDS) {
                        // Retry sending message
                        this.logger.info("Retrying sending " + messageInfo.toString());
                        message.getBody().put("retry", "true");
                        int retryMessageId = this.sendMessageTo(
                                receiver,
                                message.getType(),
                                (String[]) message.getBody().entrySet().stream()
                                        .flatMap(e -> Stream.of(e.getKey(), e.getValue())).collect(Collectors.toList())
                                        .toArray());
                        messageInfo.incrementTries();
                        messageInfo.addCorrespondingId(retryMessageId);
                    }
                }
            }
        } catch (IOException e) {
            System.err
                    .println("Error starting server on port " + this.socketAddress.toString() + ": " + e.getMessage());
        }

    }

    public int incrementAndGetMessageCounter() {
        return messageCounter.incrementAndGet();
    }

    public void finishCurrentTruck() {
        try {
            this.truck.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addMessageToQueue(Message message) {
        this.logger.fine("New message added to queue.");
        this.messageQueue.add(message);
    }

    public int sendMessageTo(SocketAddress socketAddress, String messageType, String... args) {
        // Add basic info to message body
        String[] fullArgs = new String[args.length + 6];
        int counter = 0;
        for (String s : args)
            fullArgs[counter++] = s;
        fullArgs[counter] = "truck_id";
        fullArgs[counter + 1] = String.valueOf(this.truck.getTruckId());
        fullArgs[counter + 2] = "address";
        fullArgs[counter + 3] = this.getSocketAddress().toString();
        fullArgs[counter + 4] = "receiver";
        fullArgs[counter + 5] = socketAddress.toString();

        Message message = new Message(this.incrementAndGetMessageCounter(), Utils.now(), messageType, fullArgs);
        try {
            TruckClient.sendMessage(socketAddress, message);
            // If it's message's first try, only add it to the list of unacknowledged
            // messages
            if (message.getBody().get("retry") != "true") {
                unacknowledgedSentMessages.add(new UnacknowledgedMessage(message, message.getUtc()));
            }
        } catch (IOException e) {
            this.logger.severe("Error sending message: " + e.getMessage());
        }
        return message.getId();
    }

    public SocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public LinkedBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

    public void joinPlatoonAsFollower(SocketAddress leaderAddress) {
        if (!(this.truck instanceof Truck)) {
            throw new RuntimeErrorException(null,
                    "Truck joining platoon isn't of type Truck, but " + this.truck.getClass().getSimpleName());
        }
        this.finishCurrentTruck();
        try {
            this.truck = new Follower(
                    this.truck.getTruckId(),
                    this.truck.getDirection(),
                    this.truck.getSpeed(),
                    this.truck.getDestination(),
                    this.truck.getLocation(),
                    this,
                    leaderAddress);
            this.truck.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void joinPlatoonAsPrimeFollower(SocketAddress leaderAddress, Platoon platoon) {
        if (!(this.truck instanceof Truck)) {
            throw new RuntimeErrorException(null,
                    "Truck joining platoon isn't of type Truck, but " + this.truck.getClass().getSimpleName());
        }
        this.finishCurrentTruck();
        try {
            this.truck = new PrimeFollower(
                    this.truck.getTruckId(),
                    this.truck.getDirection(),
                    this.truck.getSpeed(),
                    this.truck.getDestination(),
                    this.truck.getLocation(),
                    this,
                    platoon);
            this.truck.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
