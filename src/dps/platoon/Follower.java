package dps.platoon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dps.GPSLocation;
import dps.Message;
import dps.truck.SocketAddress;
import dps.truck.Truck;
public class Follower extends Truck {

    private Leader leader;
    private int requestInterval = 1000; // Example interval in milliseconds

    public Follower(int id, String direction, float speed, GPSLocation destination, GPSLocation location, Leader leader, SocketAddress socketAddress) throws IOException {
        super(id, direction, speed, destination, location, socketAddress);
        this.leader = leader;

        Follower thisFollower = this; // Follower instance

        // Start the leader discovery thread
        new Thread(() -> {
            while (true) {
           try {
            List<Truck> potentialLeaders = thisFollower.discoverLeaders();
            for (Truck potentialLeader : potentialLeaders) {
                String someId = "someId"; // replace with actual ID
                String someCapabilities = "someCapabilities"; // replace with actual capabilities
                Message message = new Message(someId, "request", thisFollower.getId(), potentialLeader.getId(), new HashMap<>());
                message.getBody().put("speed", String.valueOf(thisFollower.getSpeed()));
                message.getBody().put("position", thisFollower.getLocation().toString());
                message.getBody().put("capabilities", someCapabilities);
                thisFollower.sendMessageToLeader(message);
        }
        Thread.sleep(requestInterval);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
        }).start();
    }

    
    private List<Truck> discoverLeaders() {
        List<Truck> allTrucks = getAllTrucks(); // You need to implement this method to get all trucks
        List<Truck> potentialLeaders = new ArrayList<>();

        for (Truck truck : allTrucks) {
            if (truck.getSpeed() > SPEED_THRESHOLD) { // SPEED_THRESHOLD is a constant defining the minimum speed for a leader
                potentialLeaders.add(truck);
                }
            }

            return potentialLeaders;
        }
       
    }

    public void sendMessageToLeader(Message m){

        throw new UnsupportedOperationException("Unimplemented method");
    };
    
    public void disconnect(){
        throw new UnsupportedOperationException("Unimplemented method");
    };

    public void discover(){
        throw new UnsupportedOperationException("Unimplemented method");
    };