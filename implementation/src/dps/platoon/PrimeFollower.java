package dps.platoon;

import java.io.IOException;

import dps.truck.Truck;
import dps.truck.TruckCore;
import map.Direction;
import map.Location;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import dps.Message;
import dps.Utils;
import dps.truck.DatedTruckLocation;
import dps.truck.SocketAddress;

public class PrimeFollower extends Truck implements PlatoonTruck {

    private Platoon platoon; 
    private List<SocketAddress> platoonMembers;
    private final int LEADER_COMMUNICATION_TIMEOUT = 20; // seconds

    int leaderSpeed;
    DatedTruckLocation leaderTruckLocation; 
    int optimalDistanceToLeaderTail;

    public PrimeFollower(TruckCore core, Platoon platoon, int leaderSpeed, DatedTruckLocation leaderTruckLocation, int optimalDistanceToLeaderTail) throws IOException {
        super(core);
        this.platoon = platoon;
        this.leaderSpeed = leaderSpeed;
        this.leaderTruckLocation = leaderTruckLocation;
        this.optimalDistanceToLeaderTail = optimalDistanceToLeaderTail;
    }

    @Override
    public void processReceivedMessages() {
        while (true) {
            Message message = this.core.getMessageQueue().poll();
            if (message == null) {
                return;
            } else {
                this.logger.info("New message available: " + message.toString());
                String messageType = message.getType();
                Map<String, String> messageBody = message.getBody();
                switch (messageType) {
                    case "discovery":
                        this.logger.info("Discovery message received. Discarding.");    
                        break;
                    case "new_speed":
                        leaderSpeed = Integer.valueOf(messageBody.get("speed"));
                        leaderTruckLocation = DatedTruckLocation.fromString(messageBody.get("truck_location"));
                        // this.changeSpeed(leaderSpeed);
                        this.logger.info("Updated leader's speed. New leader speed: " + leaderSpeed + ", Location: " + leaderTruckLocation.toString());
                        break;
                    case "new_direction":
                        leaderTruckLocation = DatedTruckLocation.fromString(messageBody.get("truck_location"));
                        leaderTruckLocation.setColumn(Integer.valueOf(messageBody.get("target_column")));
                        this.logger.info("Updated leader's direction. New leader direction: " + leaderTruckLocation.getDirection());
                        break;
                    case "disconnect":
                        throw new IllegalArgumentException("leader_change not implemented.");
                    case "journey_end":
                        this.logger.info("Leader indicated journey has ended.");
                        truckState = "journey_end";
                        break;
                    case "ping":
                        leaderSpeed = Integer.valueOf(messageBody.get("speed"));
                        leaderTruckLocation = DatedTruckLocation.fromString(messageBody.get("truck_location"));
                    default:
                        this.logger.warning("Unknown message type: " + messageType + ". Ignoring.");
                    
                }
            }
        }
    }
    
    
    @Override
    public void run() {
        logger.info("Prime Follower started operation");
        truckState = "journey";
        while (true) {
            processReceivedMessages();
            switch (truckState) {
                case "journey":
                    Location leaderLocation = leaderTruckLocation.getHeadLocation();
                    Location truckLocation = this.getLocation();

                    // Adjust direction to match leader's column
                    int columnDifference = leaderLocation.getColumn() - truckLocation.getColumn();
                    if (columnDifference > 0){
                        this.core.setDirection(Direction.NORTH_EAST);
                    } else if (columnDifference < 0){
                        this.core.setDirection(Direction.NORTH_WEST);
                    } else {
                        this.core.setDirection(Direction.NORTH);
                    }
        
                    int newSpeed = calculateNewSpeed(this.getLocation(), leaderTruckLocation.getHeadLocation(), leaderTruckLocation.getDateTime(), this.getSpeed(), leaderSpeed, optimalDistanceToLeaderTail);
                    if (this.getSpeed() != newSpeed){
                        this.changeSpeed(newSpeed);
                    }
                    this.logger.info(String.format("Moving %s at speed %s.", this.getDirection(), this.getSpeed()));
                    // Check if leader is still in communication
                    // if (ChronoUnit.SECONDS.between(this.core.getTimeOfLastMessage(),
                    //                LocalDateTime.now()) > LEADER_COMMUNICATION_TIMEOUT) {
                    //    //leader is no longer in communication
                    //    assumeLeadership();
                    // }
                    break;
            case "journey_end":
                if (this.getSpeed() > 0) {
                    changeSpeed(0);
                }
                this.logger.info("Journey has ended. Sleeping.");
                break;
            default:
                    break;
            }
        }
    }

    private static int calculateNewSpeed(Location truckLocation, Location leaderLocation, LocalDateTime leaderLastKnownLocationTime, int currentSpeed, int leaderSpeed, int optimalDistanceToLeaderTail) {
        // Adjust position to match optimal distance
        int newSpeed = currentSpeed;
        int supposedLeaderRowChange;
        int nextLeaderRow;
        // Calculate where leader will be based on last known location and speed.
        supposedLeaderRowChange = (int) ChronoUnit.SECONDS.between(leaderLastKnownLocationTime, Utils.nowDateTime()) * leaderSpeed;
        nextLeaderRow = leaderLocation.getRow() -  supposedLeaderRowChange;
        // Calculate what our speed needs to be to catch up to leader
        int rowDifference;
        int nextRow;
        do {
            nextRow = truckLocation.getRow() - newSpeed;
            rowDifference = Math.abs(nextLeaderRow - nextRow - 1);
            // Distance too much, increase speed
            if (rowDifference > optimalDistanceToLeaderTail){
                if (newSpeed < 4){
                    newSpeed++;
                } else{
                    break;
                }
            // Distance too small, decrease speed
            } else if (rowDifference < optimalDistanceToLeaderTail){
                if (newSpeed > 0){
                    newSpeed--;
                } else{
                    break;
                }
            }
        } while (rowDifference != optimalDistanceToLeaderTail);
        return newSpeed;
    }

        private void assumeLeadership() {
            logger.info("Assuming leadership role.");
            this.truckState = "Leader";
            notifyPlatoonOfLeadershipChange();
        }
    
        private void notifyPlatoonOfLeadershipChange() {
            // Logic to notify other trucks in the platoon about the leadership change
            for (SocketAddress memberAddress : platoonMembers) {
                if (!memberAddress.equals(this.core.getSocketAddress())) {
                    // core.sendMessageTo(memberAddress, "new_leader", "leader_id", String.valueOf(this.getTruckId()));
                }
            }
            logger.info("Platoon notified of leadership change.");
        }
    
/* 
    // Send message to leader
    private void sendMessageToLeader(String string) {

        this.core.sendMessageTo(this.leaderAddress, string);
    }
*/
    

    /* 
    // Send message to all platoon members
    private void sendMessageTo(String messageType, String... messageBody) {
        for (SocketAddress memberAddress : platoonMembers) {
            if (!memberAddress.equals(this.core.getSocketAddress())) {
                core.sendMessageTo(memberAddress,messageType, messageBody);
            }
        }
    }
*/
    private void updatePlatoonMembers(String membersData) {
        // Assuming membersData is a comma-separated list of member addresses
        String[] members = membersData.split(",");
        platoonMembers.clear();
        for (String member : members) {
            platoonMembers.add(SocketAddress.fromString(member));
        }
    }


    @Override
    public void discover() {
        // Implement discovery logic
        // sending out discovery messages, updating platoon
        // information, etc.
        logger.info("Prime Follower " + this.getTruckId() + " discovery process initiated");

 }

    @Override
    public void disconnect() {
        // Implement disconnection logic
        // notifying the platoon, closing connections, etc.
        logger.info("Prime Follower " + this.getTruckId() + " disconnected");
    }

    
}
