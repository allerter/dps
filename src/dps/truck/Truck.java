package dps.truck;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import dps.CollisionSensor;
import dps.GPSLocation;
import dps.Message;

public class Truck extends Thread {
    int truckId;
    protected String truckState;
    protected Logger logger;
    AtomicInteger messageCounter = new AtomicInteger(0);
    String direction;
    double speed;
    GPSLocation destination;
    GPSLocation location;
    CollisionSensor collisionSensor;

    private SocketAddress socketAddress;
    private TruckServer server;

    public Truck(int id, String direction, float speed, GPSLocation destination, GPSLocation location, SocketAddress socketAddress) throws IOException {
        this.logger = Logger.getLogger(this.getClass().getSimpleName());
        // TODO: add that at the log message
        // text is formatted like this: Truck # - {log_message}
        this.truckState = "roaming";
        this.truckId = id;
        this.direction = direction;
        this.speed = speed;
        this.destination = destination;
        this.location = location;
        this.socketAddress = socketAddress;

        this.server = new TruckServer(socketAddress, this);
        new Thread(server).start();
    }

    public int getTruckId() {
        return truckId;
    }

    public String getTruckState() {
        return truckState;
    }

    public void setTruckState(String state) {
        this.truckState = state;
    }

    public int getMessageCounter() {
        return messageCounter.get();
    }

    public int incrementAndGetMessageCounter() {
        return messageCounter.incrementAndGet();
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public GPSLocation getDestination() {
        return destination;
    }

    public void setDestination(GPSLocation destination) {
        this.destination = destination;
    }

    public GPSLocation getLocation() {
        return location;
    }

    public void setLocation(GPSLocation location) {
        this.location = location;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void sendMessageTo(Message message, SocketAddress socketAddress) {
        try {
            TruckClient.sendMessage(socketAddress, message);
        } catch (IOException e) {
            this.logger.severe("Error sending message: " + e.getMessage());
        }
    }

    public void processReceivedMessages() {
        while (true) {
            Message message = this.server.getMessageQueue().poll();
            if (message == null) {
                return;
            } else {
                this.logger.info("New message available: " + message.toString());
            }
        }
    };

    public void run() {
        while (true) {
            processReceivedMessages();
            this.logger.info("Processed messages. Sleeping for 1 sec.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    }

}