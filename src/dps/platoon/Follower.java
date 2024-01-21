package dps.platoon;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import dps.Message;
import dps.Utils;
import dps.truck.DatedTruckLocation;
import dps.truck.SocketAddress;
import dps.truck.Truck;
import dps.truck.TruckLocation;
import dps.truck.TruckServer;
import map.Direction;
import map.Location;

public class Follower extends Truck {

    SocketAddress leaderAddress;
    int leaderSpeed;
    DatedTruckLocation leaderTruckLocation;
    int optimalDistanceToLeaderTail;

    public Follower(TruckServer server, SocketAddress leaderAddress, int leaderSpeed, DatedTruckLocation leaderTruckLocation, int optimalDistanceToLeaderTail) throws IOException {
        super(server);
        this.leaderAddress = leaderAddress;
        this.leaderSpeed = leaderSpeed;
        this.leaderTruckLocation = leaderTruckLocation;
        this.optimalDistanceToLeaderTail = optimalDistanceToLeaderTail;
    }

    public void sendMessageToLeader(Message m){
        throw new UnsupportedOperationException("Unimplemented method");
    };
    
    public void disconnect(){
        throw new UnsupportedOperationException("Unimplemented method");
    };

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
                switch (messageType) {
                    case "discovery":
                        this.logger.info("Discovery message received. Discarding.");    
                        break;
                    case "new_speed":
                        leaderSpeed = Integer.valueOf(messageBody.get("speed"));
                        leaderTruckLocation = DatedTruckLocation.fromString(messageBody.get("truck_location"));
                        this.logger.info("Adapted speed to leader's. New speed: " + this.getSpeed() + ", Location: " + leaderTruckLocation.toString());
                        break;
                    case "new_direction":
                        leaderTruckLocation = DatedTruckLocation.fromString(messageBody.get("truck_location"));
                        leaderTruckLocation.setColumn(Integer.valueOf(messageBody.get("target_column")));
                        this.logger.info("Updated leader's direction. New leader direction: " + leaderTruckLocation.getDirection());
                        break;
                    case "leader_change":
                        throw new IllegalArgumentException("leader_change not implemented.");
                    case "journey_end":
                        this.logger.info("Leader indicated journey has ended.");
                        truckState = "journey_end";
                        break;
                    default:
                        this.logger.warning("Unknown message type: " + messageType + ". Ignoring.");
                    
                }
            }
        }
    }

    @Override
    public void run() {
        this.logger.info("Started follower");
        truckState = "journey";
        while (true) {
            this.processReceivedMessages();
            switch (truckState) {
                case "journey":
                    Location leaderLocation = leaderTruckLocation.getHeadLocation();
                    Location truckLocation = this.getLocation();

                    // Adjust direction to match leader's column
                    int columnDifference = leaderLocation.getColumn() - truckLocation.getColumn();
                    if (columnDifference > 0){
                        this.server.setDirection(Direction.NORTH_EAST);
                    } else if (columnDifference < 0){
                        this.server.setDirection(Direction.NORTH_WEST);
                    } else {
                        this.server.setDirection(Direction.NORTH);
                    }
        
                    int newSpeed = calculateNewSpeed(this.getLocation(), leaderTruckLocation.getHeadLocation(), leaderTruckLocation.getDateTime(), this.getSpeed(), leaderSpeed, optimalDistanceToLeaderTail);
                    if (this.getSpeed() != newSpeed){
                        this.changeSpeed(newSpeed);
                    }
                    this.logger.info(String.format("Moving %s at speed %s.", this.getDirection(), this.getSpeed()));
                    break;
                case "journey_end":
                    if (this.getSpeed() > 0) {
                        changeSpeed(0);
                    }
                    this.logger.info("Journey has ended. Sleeping.");
                    break;
                default:
                    this.logger.warning("At unknown state:" + truckState);
                    break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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

    public void handleUnresponsiveReceiver(Message message) {
        throw new UnsupportedOperationException("Unimplemented method 'handleUnresponsiveReceiver'");
    }
}
