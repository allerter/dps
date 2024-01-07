package dps.platoon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dps.GPSLocation;
import dps.Message;
import dps.truck.SocketAddress;
import dps.truck.Truck;
import dps.Utils;

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
    
    public void removeFollower(Truck truck){

        //throw new UnsupportedOperationException("Unimplemented method");
    }
    
    public void addFollower(Truck truck){
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

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    @Override
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

