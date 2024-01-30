package dps.truck;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import dps.Message;
import dps.Utils;
import dps.platoon.Platoon;
import map.Direction;
import map.Location;

public class Truck extends Thread {
    protected String truckState;
    protected Logger logger;
    protected TruckCore core;
    protected Message referenceMessage;

    public Truck(TruckCore core) throws IOException {
        this.logger = core.getLogger();
        this.truckState = "roaming";
        this.core = core;

    }

    public int getTruckId() {
        return this.core.getTruckId();
    }

    public String getTruckState() {
        return truckState;
    }

    public void setTruckState(String state) {
        this.truckState = state;
    }

    public Direction getDirection() {
        return this.core.getDirection();
    }

    public void setDirection(Direction direction) {
        this.core.setDirection(direction);;
    }

    public Location getDestination() {
        return core.getDestination();
    }

    public void setDestination(Location destination) {
        this.core.setDestination(destination);;
    }

    public Location getLocation() {
        return core.getHeadLocation();
    }

    public TruckLocation getDirectionLocation() {
        return this.core.getLocation();
    }

    public void setLocation(Location location) {
        this.core.setLocation(location);
    }

    public int getSpeed() {
        return core.getSpeed();
    }

    public void setSpeed(int speed) {
        core.setSpeed(speed);
    }

    public void processReceivedMessages() {
        while (true) {
            Message message = this.core.getMessageQueue().poll();
            if (message == null) {
                return;
            } else {
                this.logger.info("New message available: " + message.toString());
                String messageType = message.getType();
                Map<String, String> messageBody = message.getBody();
                SocketAddress leaderAddress = SocketAddress.fromString(messageBody.get("address"));
                switch (messageType) {
                    case "discovery":
                        this.logger.info("Received invitation from Truck " + messageBody.get("truck_id") + ". Joining.");
                        this.sendMessageTo(
                            leaderAddress,
                            "join",
                            message.getId(),
                            "location",
                            this.getDirectionLocation().toString());
                        truckState = "wait_for_role";
                        break;
                    case "role":
                        String newRole = messageBody.get("role"); 
                        if (newRole.equals("follower") || newRole.equals("prime_follower")){
                            this.sendMessageTo(
                                leaderAddress, 
                                "acknowledge_role",
                                message.getId(),
                                "accepted_role",
                                newRole);
                            if (newRole.equals("follower")){
                                truckState = "join_as_follower";
                            } else {
                                truckState = "join_as_prime_follower";
                            }
                            referenceMessage = message;
                        } else {
                            this.logger.warning("Unknown role provided by potential leader. Ignoring.");
                        }

                    default:
                        break;
                }
            }
        }
    };

    protected void sendMessageTo(SocketAddress leaderAddress, String messageType, int ackId, String... messageBody) {
        this.core.sendMessageTo(leaderAddress, messageType, ackId, messageBody);
    }

    public void run() {
        LocalDateTime waitForJoinTimer = Utils.nowDateTime();
        LocalDateTime waitForRoleTimer = Utils.nowDateTime();
        truckState = "discovery";
        while (true) {
            processReceivedMessages();
            switch (truckState) {
                case "discovery":
                    if (ChronoUnit.SECONDS.between(waitForJoinTimer, Utils.nowDateTime()) > 4){
                        this.logger.info("No leader available to join. Stopping");
                        this.changeSpeed(0);
                        return;
                    }
                    break;
                case "wait_for_role":
                    if (ChronoUnit.SECONDS.between(waitForRoleTimer, Utils.nowDateTime()) > 4){
                        this.logger.info("Potential leader never sent role back. Going back to discovery.");
                        waitForJoinTimer = Utils.nowDateTime();
                        waitForRoleTimer = Utils.nowDateTime();;
                        truckState = "discovery";
                    }
                    break;
                case "communication_lost":
                    this.logger.info("Communication is lost. Stopping.");
                    this.changeSpeed(0);
                    return;
                case "join_as_follower":
                case "join_as_prime_follower":
                    this.logger.info("Platoon found. Leaving Truck role.");
                    return;
                default:
                    break;
            }
            // this.logger.info("Processed messages as " + this.getClass().getSimpleName() + ". Sleeping for 1 sec.");
        }
    }

    protected void changeDirection(Direction newDirection) {
        this.logger.info("Changing direction to " + newDirection);
        this.setDirection(newDirection);
    }

    public void changeSpeed(int newSpeed) {
        this.logger.info("Changing speed from " + this.getSpeed() + " to " + newSpeed);
        this.setSpeed(newSpeed);
    }

    public void handleUnresponsiveReceiver(Message message) {
        truckState = "communication_lost";
    }
}