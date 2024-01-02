package dps;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Truck extends Thread  {
    int truckId;
    protected String truckState;
    protected Logger logger;
    Queue<Message> messageQueue;
    AtomicInteger messageCounter = new AtomicInteger(0);
    String direction;
    double speed;
    GPSLocation destination;
    GPSLocation location;
    CollisionSensor collisionSensor;


    public Truck(int id, String direction, float speed, GPSLocation destination, GPSLocation location) {
        this.logger = Logger.getLogger(this.getClass().getSimpleName());
        // TODO: add that at the log message
        // text is formatted like this: Truck # - {log_message}
        this.truckState = "roaming";
        this.truckId = id;
        this.direction = direction;
        this.speed = speed;
        this.destination = destination;
        this.location = location;
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

    public int incrementAndGetMessageCounter(){
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

    public void processReceivedMessages(){
        throw new UnsupportedOperationException("Unimplemented method");
    };

    public void run(){
        throw new UnsupportedOperationException("Unimplemented method");
    }

}