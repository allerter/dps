package dps.truck;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import dps.Message;
import dps.platoon.Platoon;
import map.Direction;
import map.Location;

public class Truck extends Thread {
    protected String truckState;
    protected Logger logger;
    protected TruckServer server;
    

    public Truck(TruckServer server) throws IOException {
        this.logger = Logger.getLogger(this.getClass().getSimpleName());
        this.truckState = "roaming";
        this.server = server;

    }

    public int getTruckId() {
        return this.server.getTruckId();
    }

    public String getTruckState() {
        return truckState;
    }

    public void setTruckState(String state) {
        this.truckState = state;
    }

    public Direction getDirection() {
        return this.server.getDirection();
    }

    public void setDirection(Direction direction) {
        this.server.setDirection(direction);;
    }

    public Location getDestination() {
        return server.getDestination();
    }

    public void setDestination(Location destination) {
        this.server.setDestination(destination);;
    }

    public Location getLocation() {
        return server.getHeadLocation();
    }

    public TruckLocation getDirectionLocation() {
        return this.server.getLocation();
    }

    public void setLocation(Location location) {
        this.server.setLocation(location);
    }

    public double getSpeed() {
        return server.getSpeed();
    }

    public void setSpeed(double speed) {
        server.setSpeed(speed);
    }

    public void processReceivedMessages() {
        while (true) {
            Message message = this.server.getMessageQueue().poll();
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
                            "location",
                            this.getDirectionLocation().toString());
                        truckState = "wait_for_role";
                        break;
                    case "new_role":
                        String newRole = messageBody.get("role"); 
                        if (newRole == "follower" || newRole == "prime_follower"){
                            this.sendMessageTo(
                                leaderAddress, 
                                "acknowledge_role",
                                "accepted_role",
                                newRole);
                            if (newRole == "follower"){
                                this.server.joinPlatoonAsFollower(leaderAddress);
                            } else {
                                
                                Platoon platoon = Platoon.fromJson(messageBody.get("platoon"));

                                this.server.joinPlatoonAsPrimeFollower(leaderAddress, platoon);
                            }
                            truckState = "join";
                        } else {
                            this.logger.warning("Unknown role provided by potential leader. Ignoring.");
                        }

                    default:
                        break;
                }
            }
        }
    };

    protected void sendMessageTo(SocketAddress leaderAddress, String messageType, String... messageBody) {
        this.server.sendMessageTo(leaderAddress, messageType, messageBody);
    }

    public void run() {
        int waitForJoin = 0;
        int waitForRole = 0;
        while (true) {
            processReceivedMessages();
            truckState = "discovery";
            switch (truckState) {
                case "discovery":
                    if (waitForJoin > 4){
                        this.logger.info("No leader available to join. Stopping");
                        this.changeSpeed(0);
                        return;
                    }
                    waitForJoin++;
                    break;
                case "wait_for_role":
                    if (waitForRole > 4){
                        this.logger.info("Potential leader never sent role back. Going back to discovery.");
                        waitForJoin = 0;
                        waitForRole = 0;
                        truckState = "discovery";
                    }
                    break;
                case "communication_lost":
                    this.logger.info("Communication is lost. Stopping.");
                    this.changeSpeed(0);
                    return;
                case "join":
                    this.logger.info("Platoon found. Leaving Truck role.");
                    return;
                default:
                    break;
            }
            this.logger.info("Processed messages. Sleeping for 1 sec.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected void changeDirection(Direction newDirection) {
        this.setDirection(newDirection);
    }

    protected void changeSpeed(double newSpeed) {
        this.setSpeed(newSpeed);
    }

    public void handleUnresponsiveReceiver(Message message) {
        truckState = "communication_lost";
    }
}