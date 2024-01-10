package dps.platoon;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dps.GPSLocation;
import dps.Message;
import dps.truck.SocketAddress;
import dps.truck.Truck;
import dps.truck.TruckServer;
import dps.Utils;
import dps.platoon.Leader;

import java.time.Duration;

public class PrimeFollower extends Truck implements PlatoonTruck {

    private Platoon platoon;
    private boolean isLeader = false;
    private LocalDateTime lastLeaderPingTime;
    private static final int LEADER_COMMUNICATION_TIMEOUT = 30; // in seconds
    private List<SocketAddress> platoonMembers;

    private int leaderId;// Id of current leader
    private SocketAddress leaderAddress;
    private ArrayList<SocketAddress> platoonAddresses = new ArrayList<>();

    private TruckState truckState;

    public PrimeFollower(int id, String direction, double speed, GPSLocation destination, GPSLocation location,
            TruckServer server, Platoon platoon) throws IOException {
        super(id, direction, speed, destination, location, server);
        this.platoon = platoon;
        this.platoonAddresses = new ArrayList<>();
        this.lastLeaderPingTime = LocalDateTime.now(); // Initialize last ping time
    }

    private void notifyPlatoonOfLeadershipChange() {
        // Logic to notify other trucks in the platoon about the leadership change
        for (SocketAddress memberAddress : platoonMembers) {
            if (!memberAddress.equals(this.server.getSocketAddress())) {
                server.sendMessageTo(memberAddress, "new_leader", "leader_id", String.valueOf(this.getTruckId()));
            }
        }
        logger.info("Platoon notified of leadership change.");
    }

    @Override
    public void run() {
        logger.info("Prime Follower started operation");
        while (true) {
            processReceivedMessages();

            // Check if leader is still in communication
            if (lastLeaderPingTime != null &&
                    ChronoUnit.SECONDS.between(lastLeaderPingTime,
                            LocalDateTime.now()) > LEADER_COMMUNICATION_TIMEOUT) {
                //leader is no longer in communication
                assumeLeadership();
            }

            try {
                Thread.sleep(1000); // Sleep for a while before next cycle
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // interrupt status
                logger.severe("PrimeFollower interrupted: " + e.getMessage());
                break;
            } catch (Exception e) {
                logger.severe("Error in PrimeFollower run loop: " + e.getMessage());
                // Handle other exceptions appropriately
            }
        }
    }

    private void assumeLeadership() {
        logger.info("Assuming leadership role.");
        this.truckState = "Leader";
        notifyPlatoonOfLeadershipChange();
    }

    private void sendMessageToLeader(String string) {

    }

    @Override
    public void processReceivedMessages() {
        super.processReceivedMessages(); // Handle truck messages

        while (true) {
            Message message = this.server.getMessageQueue().poll();
            if (message == null) {
                return;
            } else {
                this.logger.info("Prime Follower received message: " + message.toString());
                String messageType = message.getType();
                Map<String, String> messageBody = message.getBody();

                switch (messageType) {
                    case "leader_ping":
                        // Update the last time we heard from the leader
                        this.lastLeaderPingTime = LocalDateTime.now();
                        break;
                    case "platoon_update":
                        // Update platoon members list
                        updatePlatoonMembers(messageBody.get("platoon_members"));
                        break;
                    // Add more cases as per your message types
                    default:
                        break;
                }
            }
        }
    }

    private void updatePlatoonMembers(String membersData) {
        // Assuming membersData is a comma-separated list of member addresses
        String[] members = membersData.split(",");
        platoonMembers.clear();
        for (String member : members) {
            platoonMembers.add(SocketAddress.fromString(member));
        }
    }

    /* 
 // Message to send to other trucks in the platoon
  
    private void sendMessageTo(String messageType, String... messageBody) {
        for (SocketAddress memberAddress : platoonMembers) {
            if (!memberAddress.equals(this.server.getSocketAddress())) {
                server.sendMessageTo(memberAddress, messageType, messageBody);
            }
        }
    }
    */



    @Override
    public void disconnect() {

        // Implement disconnection logic
        // notifying the platoon, closing connections, etc.
        logger.info("Prime Follower " + this.getTruckId() + " disconnected");
        // throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
    }

    @Override
    public void discover() {

        // Implement discovery logic
        // sending out discovery messages, updating platoon
        // information, etc.
        logger.info("Prime Follower " + this.getTruckId() + " discovery process initiated");

        // throw new UnsupportedOperationException("Unimplemented method 'discover'");
    }
}