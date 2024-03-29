package dps.truck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.management.RuntimeErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import dps.CollisionSensor;
import dps.Message;
import dps.Utils;
import dps.platoon.Follower;
import dps.platoon.Platoon;
import dps.platoon.PrimeFollower;
import map.Direction;
import map.GridMap;
import map.Location;

public class TruckCore extends Thread {
    // Truck-related
    private Truck role;
    private int truckId;
    private int speed;
    private TruckLocation location;
    private Location destination;
    private CollisionSensor collisionSensor;

    // Network related
    private SocketAddress socketAddress;
    private LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private AtomicInteger messageCounter = new AtomicInteger(0);
    private List<UnacknowledgedMessage> unacknowledgedSentMessages = Collections.synchronizedList(new ArrayList<UnacknowledgedMessage>());
    private LocalDateTime timeOfLastMessage = Utils.nowDateTime();
    private int[] clockMatrix = new int[4];

    // General
    final static int MAX_RETRIES = 3;
    final static int WAIT_BEFORE_TRY_SECONDS = 10;
    private Logger logger;
    private GridMap map;
    
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

    public TruckCore(
        int id,
        int speed,
        TruckLocation location,
        Location destination,
        SocketAddress socketAddress,
        GridMap map) throws IOException {
        this.truckId = id;
        this.logger = Logger.getLogger("dps." + this.toString());
        this.speed = speed;
        this.location = location;
        this.destination = destination;
        this.socketAddress = socketAddress;
        this.map = map;
    }

    public int getTruckId() {
        return truckId;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public TruckLocation getLocation() {
        int[] currentLocation = map.findElement("H" + this.truckId);
        location.setRow(currentLocation[0]);
        location.setColumn(currentLocation[1]);
        return location;
    }

    public Location getHeadLocation(){
        int[] currentLocation = map.findElement("H" + this.truckId);
        location.setRow(currentLocation[0]);
        location.setColumn(currentLocation[1]);
        return location.getHeadLocation();
    }

    public void setLocation(Location location) {
        this.location.setRow(location.getRow());
        this.location.setColumn(location.getColumn());
    }


    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public void setRole(Truck role) {
        this.role = role;
        role.start();
    }

    public Truck getRole() {
        return role;
    }
    public Direction getDirection(){
        return this.location.getDirection();
    }
    public void setDirection(Direction direction){
        this.location.setDirection(direction);
    }

    public Logger getLogger() {
        return logger;
    }

    public LocalDateTime getTimeOfLastMessage() {
        return timeOfLastMessage;
    }

    @Override
    public String toString() {
        return String.format("Truck %d", this.truckId);
    }

    public int[] getClockMatrix(){
        return this.clockMatrix;
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
                    int ackId = receivedMessage.getAckId();
                    ArrayList<UnacknowledgedMessage> acknowledgedMessages = new ArrayList<>();
                    for (UnacknowledgedMessage messageInfo : unacknowledgedSentMessages) {
                        if (messageInfo.getCorrespondingIdsList().contains(ackId)) {
                            acknowledgedMessages.add(messageInfo);
                            this.logger.fine(messageInfo.toString() + " was acknowledged.");
                            break;
                        }
                    }
                    unacknowledgedSentMessages.removeAll(acknowledgedMessages);

                    this.updateClockMatrix(
                        Utils.stringToClockMatrixArray(
                            receivedMessage.getBody().get("clock_matrix"))
                    );
                    this.addMessageToQueue(receivedMessage);

                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error in communication with the client: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Check if messages from before have been acknowledged
                for (UnacknowledgedMessage messageInfo : unacknowledgedSentMessages) {
                    Message message = messageInfo.message;
                    int messageTries = messageInfo.tries;
                    SocketAddress receiver = messageInfo.receiver;
                    if (messageTries == MAX_RETRIES) {
                        this.logger.info("Message has not been acknowledged after 3 tries. Alerting truck.");
                        this.role.handleUnresponsiveReceiver(message);
                    }
                    if (messageTries < MAX_RETRIES && ChronoUnit.SECONDS.between(messageInfo.lastTry,
                            LocalDateTime.now()) >= WAIT_BEFORE_TRY_SECONDS) {
                        // Retry sending message
                        this.logger.info("Retrying sending " + messageInfo.toString());
                        message.getBody().put("retry", "true");
                        int retryMessageId = this.sendMessageTo(
                                receiver,
                                message.getType(),
                                message.getAckId(),
                                message.getBody().entrySet().stream()
                                        .flatMap(e -> Stream.of(e.getKey(), e.getValue())).collect(Collectors.toList())
                                        .toArray(new String[message.getBody().keySet().size()]));
                        messageInfo.incrementTries();
                        messageInfo.addCorrespondingId(retryMessageId);
                    }
                }

                // Manage cases where truck thread has exited
                if (!this.role.isAlive()) {
                    this.logger.finer("Truck isn't alive. Handling...");
                    if (this.role.truckState.equals("join_as_follower")){
                        SocketAddress leaderSocketAddress = SocketAddress.fromString(this.role.referenceMessage.getBody().get("address"));
                        int leaderSpeed = Integer.valueOf(this.role.referenceMessage.getBody().get("speed"));
                        DatedTruckLocation leaderTruckLocation = DatedTruckLocation.fromString(this.role.referenceMessage.getBody().get("truck_location"));
                        int optimalDistanceToLeaderTail = Integer.valueOf(this.role.referenceMessage.getBody().get("optimal_distance"));
                        this.joinPlatoonAsFollower(leaderSocketAddress, leaderSpeed, leaderTruckLocation, optimalDistanceToLeaderTail);
                    }
                    else if (this.role.truckState.equals("join_as_prime_follower")){
                        int leaderSpeed = Integer.valueOf(this.role.referenceMessage.getBody().get("speed"));
                        DatedTruckLocation leaderTruckLocation = DatedTruckLocation.fromString(this.role.referenceMessage.getBody().get("truck_location"));
                        Platoon platoon = Platoon.fromJson(this.role.referenceMessage.getBody().get("platoon"));
                        int optimalDistanceToLeaderTail = Integer.valueOf(this.role.referenceMessage.getBody().get("optimal_distance"));
                        this.joinPlatoonAsPrimeFollower(platoon, leaderSpeed, leaderTruckLocation, optimalDistanceToLeaderTail);
                    } else {
                        throw new IllegalArgumentException("Truck exited with undefined role: " + this.role.truckState);
                    }
                }

            }
        } catch (IOException e) {
            System.err
                    .println("Error starting server on port " + this.socketAddress.toString() + ": " + e.getMessage());
        }

    }

    private void updateClockMatrix(int[] receivedClockMatrix) {
        for (int i = 0; i < clockMatrix.length; i++) {
            if (clockMatrix[i] < receivedClockMatrix[i]){
                if (i == truckId){
                    this.logger.severe("Own clock tick behind the assumption of other truck.");
                } else {
                    clockMatrix[i] = receivedClockMatrix[i];
                }
            }
        }
        this.logger.info(Arrays.toString(clockMatrix));
    }

    public int incrementAndGetMessageCounter() {
        // Increment own clock cycle
        clockMatrix[truckId]++;
        return messageCounter.incrementAndGet();
    }

    public void finishCurrentTruck() {
        try {
            this.role.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addMessageToQueue(Message message) {
        this.logger.fine("New message added to queue.");
        this.messageQueue.add(message);
    }

    public int sendMessageTo(SocketAddress socketAddress, String messageType, int ackId, String... args) {
        int messageId = this.incrementAndGetMessageCounter();
        // Add basic info to message body
        String[] fullArgs = new String[args.length + 8];
        int counter = 0;
        for (String s : args)
            fullArgs[counter++] = s;
        fullArgs[counter] = "truck_id";
        fullArgs[counter + 1] = String.valueOf(this.getTruckId());
        fullArgs[counter + 2] = "address";
        fullArgs[counter + 3] = this.getSocketAddress().toString();
        fullArgs[counter + 4] = "receiver";
        fullArgs[counter + 5] = socketAddress.toString();
        fullArgs[counter + 6] = "clock_matrix";
        fullArgs[counter + 7] = Utils.clockMatrixArrayToString(clockMatrix);

        Message message = new Message(messageId, Utils.now(), messageType, ackId, fullArgs);
        try {
            TruckClient.sendMessage(socketAddress, message);
            // If it's message's first try, only add it to the list of unacknowledged
            // messages
            if (message.getBody().get("retry") != "true") {
                unacknowledgedSentMessages.add(new UnacknowledgedMessage(message, message.getUtc()));
            }
            this.logger.finer("Sent message to " + socketAddress.toString());
        } catch (IOException e) {
            this.logger.severe("Error sending message: " + e.getMessage());
        }
        timeOfLastMessage = Utils.nowDateTime();
        return message.getId();
    }

    public SocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public LinkedBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

    public void joinPlatoonAsFollower(SocketAddress leaderAddress, int leaderSpeed, DatedTruckLocation leaderTruckLocation, int optimalDistanceToLeaderTail) {
        this.logger.info("Joining platoon as follower");
        if (!(this.role instanceof Truck)) {
            throw new RuntimeErrorException(null,
                    "Truck joining platoon isn't of type Truck, but " + this.role.getClass().getSimpleName());
        }
        this.finishCurrentTruck();
        try {
            this.role = new Follower(
                    this,
                    leaderAddress,
                    leaderSpeed,
                    leaderTruckLocation,
                    optimalDistanceToLeaderTail);
            this.role.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void joinPlatoonAsPrimeFollower(Platoon platoon, int leaderSpeed, DatedTruckLocation leaderTruckLocation, int optimalDistanceToLeaderTail) {
        this.logger.info("Joining platoon as prime follower");
        if (!(this.role instanceof Truck)) {
            throw new RuntimeErrorException(null,
                    "Truck joining platoon isn't of type Truck, but " + this.role.getClass().getSimpleName());
        }
        this.finishCurrentTruck();
        try {
            this.role = new PrimeFollower(
                    this,
                    platoon,
                    leaderSpeed,
                    leaderTruckLocation,
                    optimalDistanceToLeaderTail
                    );
            this.role.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void incrementSpeed() {
        this.speed++;
    }
}
