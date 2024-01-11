package dps.truck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.management.RuntimeErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import dps.Message;
import dps.Utils;
import dps.platoon.Follower;
import dps.platoon.Platoon;
import dps.platoon.PrimeFollower;

public class TruckServer extends Thread {
    private Logger logger;
    private SocketAddress socketAddress;
    private Truck truck;
    LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    AtomicInteger messageCounter = new AtomicInteger(0);

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
                            String receivedMessage = rStringBuilder.toString();
                            this.addMessageToQueue(Message.fromJson(receivedMessage));
                
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error in communication with the client: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                if (!this.truck.isAlive()){
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server on port " + this.socketAddress.toString() + ": " + e.getMessage());
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


    public void sendMessageTo(SocketAddress socketAddress, String messageType, String... args) {
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
        } catch (IOException e) {
            this.logger.severe("Error sending message: " + e.getMessage());
        }
    }

    public SocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public LinkedBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

    public void joinPlatoonAsFollower(SocketAddress leaderAddress) {
        if (!(this.truck instanceof Truck)){
            throw new RuntimeErrorException(null, "Truck joining platoon isn't of type Truck, but " + this.truck.getClass().getSimpleName());
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
                leaderAddress
                );
                this.truck.start();
            } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void joinPlatoonAsPrimeFollower(SocketAddress leaderAddress, Platoon platoon) {
        if (!(this.truck instanceof Truck)){
            throw new RuntimeErrorException(null, "Truck joining platoon isn't of type Truck, but " + this.truck.getClass().getSimpleName());
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
                platoon
                );
                this.truck.start();
            } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
