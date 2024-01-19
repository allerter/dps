package dps.platoon;

import java.io.IOException;
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
    int optimalDistanceToLeader;

    public Follower(TruckServer server, SocketAddress leaderAddress, int leaderSpeed, DatedTruckLocation leaderTruckLocation, int optimalDistanceToLeader) throws IOException {
        super(server);
        this.leaderAddress = leaderAddress;
        this.leaderSpeed = leaderSpeed;
        this.leaderTruckLocation = leaderTruckLocation;
        this.optimalDistanceToLeader = optimalDistanceToLeader;
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
                    case "direction_change":
                        this.changeDirection(Direction.valueOf(messageBody.get("direction")));
                        this.logger.info("Adapted speed to leader's. New speed: " + this.getSpeed());
                        break;
                    case "leader_change":
                        throw new IllegalArgumentException("leader_change not implemented.");  
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
        
                    // Adjust speed to match leader's
                    int currentSpeed = this.getSpeed();
                    if (currentSpeed < leaderSpeed){
                        this.increaseSpeed(leaderSpeed - currentSpeed);
                    } else if (currentSpeed > leaderSpeed){
                        this.reduceSpeed(currentSpeed - leaderSpeed);
                    }
                    
                    // Adjust direction to match leader's column
                    int columnDifference = leaderLocation.getColumn() - truckLocation.getColumn();
                    if (columnDifference > 0){
                        this.server.setDirection(Direction.NORTH_EAST);
                    } else if (columnDifference < 0){
                        this.server.setDirection(Direction.NORTH_WEST);
                    }
        
                    // Adjust position to match optimal distance
                    int rowDifference = leaderLocation.getRow() - truckLocation.getRow();
                    if (rowDifference < optimalDistanceToLeader){
                        this.reduceSpeed(rowDifference);
                    } else if (rowDifference > optimalDistanceToLeader){
                        this.increaseSpeed(rowDifference);
                    }
                    break;
            
                default:
                    break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }        }
    }

    private void reduceSpeed(int newSpeed) {
        this.server.setSpeed(newSpeed);
    }

    private void increaseSpeed(int newSpeed) {
        this.server.setSpeed(newSpeed);
    }

    public void handleUnresponsiveReceiver(Message message) {
        throw new UnsupportedOperationException("Unimplemented method 'handleUnresponsiveReceiver'");
    }
}
