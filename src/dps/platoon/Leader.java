package dps.platoon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dps.GPSLocation;
import dps.Message;
import dps.truck.SocketAddress;
import dps.truck.Truck;
import dps.Utils;
import dps.platoon.Platoon;

public class Leader extends Truck implements PlatoonTruck{

    private Platoon platoon;
    private SocketAddress[] trucksAddresses;

    public Leader(int id, String direction, float speed, GPSLocation destination, GPSLocation location, SocketAddress socketAddress, SocketAddress[] otherTrucks) throws IOException {
        super(id, direction, speed, destination, location, socketAddress);
        this.trucksAddresses = otherTrucks;
    }

    public Platoon getPlatoon() {
        return platoon;
    }

    public void setPlatoon(Platoon platoon) {
        this.platoon = platoon;
    }
    // Declare the followers variable
     Set<Truck> followers,
    // Create a set of followers
    followers = new HashSet<>();
    // Set the broadcast interval to some value (in milliseconds)
     int broadcastInterval = 1000;
    // Start a thread that broadcasts the status and commands to the followers periodically
    //  new Thread(new Runnable() {
    
    //     public void run() {
    //                     while (true) {
    //                         try {
    //                             // Create a message object with a unique ID, type, sender, receiver, and body
    //                            Message message = new Message(/* some ID */, "broadcast", this.id, null, null);
    //                            // Add the status and commands of the leader truck to the body of the message
    //                            message.body = new HashMap<>();
    //                            message.body.put("speed", String.valueOf(this.speed));
    //                            message.body.put("position", String.valueOf(this.location));
    //                            message.body.put("direction", this.direction);
    //                            message.body.put("command", /* some command */);
    //                            // Send the message to all the followers
    //                            for (Truck follower : followers) {
    //                                sendMessage(message, follower.ipAddress, follower.port);
    //                            }
    //                            // Wait for the broadcast interval
    //                            Thread.sleep(broadcastInterval);
    //                        } catch (InterruptedException e) {
    //                            // Handle any exceptions
    //                            e.printStackTrace();
    //                        }
    //                    }
    //                }

    // @Override
    // public void disconnect() {
    //     // TODO
    // }

    // @Override
    // public void discover() {
    //     // TODO
    // }
    //            }).start();
    //        }
        
           // Override the receiveMessage method
        
           void receiveMessage(Message message) {
               // Process the message according to its type and content
               switch (message.type) {
                   // Define different cases for different message types, such as request, response, status, etc.
                   // For example, if the message type is request, check the sender, receiver, and body of the message, and send a response accordingly
                   case "request":
                       if (message.receiver.equals(this.id)) {
                           // Create a response message with the same ID and sender and receiver swapped
                           Message response = new Message("response", this.id, message.sender, null);
                           response.body = new HashMap<>();
                           if (/* some condition */) {
                               response.body.put("status", "accepted");
                               response.body.put("gap", 15;
                               response.body.put("speed", 100);
                               // Add the sender to the set of followers and the platoon
                               followers.add(null/* some truck object */);
                               platoon.addFollower(/* some truck object */);
                           } else {
                               response.body.put("status", "rejected");
                               response.body.put("reason", /* some reason */);
                           }
                           // Send the response message to the sender
                           sendMessage(response, message.sender, /* some port */);
                       }
                       break;
                   // Define other cases for other message types
                   // For example, if the message type is status, check the sender, receiver, and body of the message, and update the platoon accordingly
                   case "status":
                       if (message.receiver.equals(this.id)) {
                           // Check the body of the status message for the information and feedback of the sender
                           // For example, if the body contains the speed, position, and fuel level of the sender, compare them with your own and adjust the platoon accordingly
                           // Update the platoon with the new information of the sender
                           platoon.addFollower(/* some truck object */);
                       }
                       break;
                   // Define default case for unknown message types
                   default:
                       // Do nothing or log an error
                       break;
               }
           }
        

    public void broadcast(){
        for (SocketAddress truckAddress : trucksAddresses) {
            this.logger.finer("Sending message to " + truckAddress.toString());
            Message message = new Message(
                incrementAndGetMessageCounter(),
                Utils.now(),
                "discovery",
                "truck_id",
                Integer.toString(this.getTruckId()),
                "address",
                this.getSocketAddress().toString()
            );
            this.sendMessageTo(message, truckAddress);
        }
    }
    
    public void addFollower(Truck follower){
        if (!platoon.contains(follower)) {
            // Create a message object with a unique ID, type, sender, receiver, and body
            Message message = new Message(/* some ID */, "join", this.id,follower.id,null);
            // platoon ID, size, speed, and position to the body of the message
            message.body = new HashMap<>();
            message.body.put("platoonID", platoon.id);
            message.body.put("platoonSize", String.valueOf(platoon.size()));
            message.body.put("platoonSpeed", String.valueOf(platoon.speed));
            message.body.put("platoonPosition", String.valueOf(platoon.position));
            // Send the message to the follower
            sendMessage(message); 
            platoon.add(follower);
            for (SocketAddress truckAddress : trucksAddresses) {
                sendMessage(message, truckAddress.ipAddress, truckAddress.port);
            }
        }
       // throw new UnsupportedOperationException("Unimplemented method");
    }

    public void removeFollower(Truck follower){
        if (followers.contain)



        //throw new UnsupportedOperationException("Unimplemented method");
    }
    
    public void startPlatoon(){
        boolean isPlatooning;
        if (!platoon.isEmpty() && !isPlatooning) {
            isPlatooning = true;
            // Create a message object with a unique ID, type, sender, receiver, and body
            Message message = new Message(/* some ID */, "start", this.id, null, null);
            // platoon ID, size, speed, and position to the body of the message
            message.body = new HashMap<>();
            message.body.put("platoonID", platoon.id); 
            message.body.put("platoonSize", String.valueOf(platoon.size()));
            message.body.put("platoonSpeed", String.valueOf(platoon.speed));
            message.body.put("platoonPosition", String.valueOf(platoon.position));
            // Send the message to all the trucksAddresses
            for (SocketAddress truckAddress : trucksAddresses) {
                sendMessage(message, truckAddress.ipAddress, truckAddress.port);
            }
        }
       // throw new UnsupportedOperationException("Unimplemented method");
    };
    
    public void discover(){
        throw new UnsupportedOperationException("Unimplemented method");
    };

    //@Override
    public void disconnect() {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    //@Override
    public void run() {
        logger.info("Beginning operation.");
        int waitForJoin = 0;
        ArrayList<Truck> joinedTrucks = new ArrayList<Truck>();
        truckState = "discovery";
        while (true) {
            processReceivedMessages();
            switch (truckState) { 
                case "discovery":
                    // Wait 5 seconds for other trucks to join
                    if (waitForJoin == 5 && joinedTrucks.size() != 3) {
                        logger.info("Discovery unsuccessful. Trucked joined: " + joinedTrucks.size());
                        truckState = "roaming";
                    // All trucks join. Start the journey
                    } else if (joinedTrucks.size() == 3) {
                        logger.info("Discovery successful. Starting journey...");
                        truckState = "journey";
                    } else {
                        this.logger.info("Sending discovery message...");
                        // Send discovery message to all trucks
                        broadcast();
                    }                    
                    waitForJoin++;
                    break;            
                default:
                    this.logger.severe("Truck in unknown truckState: " + truckState);
                    break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.severe(e.getStackTrace().toString());
            }
        }
    }
}

