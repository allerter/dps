package dps.platoon;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;

import dps.GPSLocation;
import dps.Message;
import dps.truck.SocketAddress;
import dps.truck.Truck;
import dps.truck.TruckServer;
import dps.Utils;

public class Leader extends Truck implements PlatoonTruck {

    private ArrayList<SocketAddress> orderedPlatoonSocketAddresses = new ArrayList<>();
    Platoon platoon;
    ArrayList<PotentialFollowerInfo> joinedTrucksList = new ArrayList<>();
    SocketAddress[] otherTrucksSocketAddresses;

    class PotentialFollowerInfo {
        public PotentialFollowerInfo(int senderId, GPSLocation fromString, SocketAddress senderAddress) {
        }

        int id;
        GPSLocation location;
        SocketAddress address;
    }

    public Leader(int id, String direction, double speed, GPSLocation destination, GPSLocation location,
            TruckServer server, SocketAddress[] otherTrucksSocketAddresses) throws IOException {
        super(id, direction, speed, destination, location, server);
        this.otherTrucksSocketAddresses = otherTrucksSocketAddresses;
    }

    public Leader(int id, String direction, double speed, GPSLocation destination, GPSLocation location,
            TruckServer server, Platoon platoon) throws IOException {
        super(id, direction, speed, destination, location, server);
        this.platoon = platoon;
    }

    public void broadcast(SocketAddress[] truckSocketAddresses, String messageType, String... args) {
        this.logger.info("Sending discovery message to trucks.");

        for (SocketAddress truckAddress : truckSocketAddresses) {
            this.logger.finer("Sending " + messageType + " message to " + truckAddress.toString());
            this.server.sendMessageTo(truckAddress, messageType, args);
        }
    }

    public void removeFollower(Truck truck) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public void addFollower(Truck truck) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public void startPlatoon() {
        throw new UnsupportedOperationException("Unimplemented method");
    };

    public void discover() {
        throw new UnsupportedOperationException("Unimplemented method");
    };

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    @Override
    public void processReceivedMessages() {
        while (true) {
            Message message = this.server.getMessageQueue().poll();
            if (message == null) {
                return;
            } else {
                this.logger.info("New message available: " + message.toString());
                String messageType = message.getType();
                Map<String, String> messageBody = message.getBody();
                SocketAddress senderAddress = SocketAddress.fromString(messageBody.get("address"));
                int senderId = Integer.valueOf(messageBody.get("truck_id"));
                switch (messageType) {
                    case "join":
                        PotentialFollowerInfo newTruckInfo = new PotentialFollowerInfo(senderId,
                                GPSLocation.fromString(messageBody.get("location")), senderAddress);
                        this.assignRoleToNewTruck(newTruckInfo);
                    default:
                        break;
                }
            }
        }
    }

    private void assignRoleToNewTruck(PotentialFollowerInfo newTruckInfo) {
        joinedTrucksList.add(newTruckInfo);
        if (truckState != "journey" && orderedPlatoonSocketAddresses.size() < 3) {
            this.logger.info("Not enough trucks in platoon. Won't assign role to new truck until more arrive.");
        } else if (truckState == "journey") {

        } else {
            // sort joined trucks based on distance to leader
            joinedTrucksList.sort(
                    (t1, t2) -> (int) (Utils.distance(this.getLocation(), t1.location)
                            - Utils.distance(this.getLocation(), t2.location)));
            PotentialFollowerInfo primeFollower = joinedTrucksList.remove(0);

            // closest truck is prime follower
            this.sendMessageTo(primeFollower.address, "role", "role", "prime_follower");

            for (PotentialFollowerInfo potentialFollowerInfo : joinedTrucksList) {
                this.sendMessageTo(potentialFollowerInfo.address, "role", "role", "follower");
            }
            truckState = "journey";
        }
    }

    @Override
    public void run() {
        logger.info("Beginning operation.");

        if (this.platoon == null) {
            // Send discovery message to all trucks
            this.broadcast(otherTrucksSocketAddresses, "discovery");
        }

        int waitForJoin = 0;
        truckState = "discovery";
        LocalDateTime timeOfLastMessage = Utils.nowDateTime();
        while (true) {
            processReceivedMessages();
            switch (truckState) {
                case "discovery":
                    // Wait 5 seconds for other trucks to join
                    if (waitForJoin == 5 && joinedTrucksList.size() != 3) {
                        logger.info("Discovery unsuccessful. Trucked joined: " + joinedTrucksList.size());
                        truckState = "roaming";
                        // All trucks join. Start the journey
                    } else {
                        this.logger.info("Waiting for trucks to join...");
                    }
                    waitForJoin++;
                    break;
                case "journey":
                    if (ChronoUnit.SECONDS.between(timeOfLastMessage, Utils.nowDateTime()) >= 5) {
                        this.broadcast(platoon.getSocketAddresses(), "ping");
                    }
                    this.logger.info("Moving to destination at speed " + this.getSpeed());
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