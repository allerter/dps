package dps.platoon;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import dps.Message;
import dps.truck.SocketAddress;
import dps.truck.Truck;
import dps.truck.TruckLocation;
import dps.truck.TruckCore;
import map.Direction;
import map.GridMap;
import map.Location;
import dps.Utils;

public class Leader extends Truck implements PlatoonTruck{

    int DISTANCE_BETWEEN_TRUCKS = 2;
    int WAIT_BEFORE_PING = 10;
    public int JOURNEY_SPEED = 2;
    Platoon platoon;
    ArrayList<PotentialFollowerInfo> joinedTrucksList = new ArrayList<>();
    SocketAddress[] otherTrucksSocketAddresses;

    class PotentialFollowerInfo {
        int id;
        TruckLocation location;
        SocketAddress address;
        Message originatingMessage;
        public PotentialFollowerInfo(int senderId, TruckLocation location, SocketAddress senderAddress, Message originatingMessage) {
            this.id = senderId;
            this.location = location;
            this.address = senderAddress;
            this.originatingMessage = originatingMessage;
        }
    }

    public Leader(TruckCore core, SocketAddress[] otherTrucksSocketAddresses) throws IOException {
        super(core);
        this.otherTrucksSocketAddresses = otherTrucksSocketAddresses;
    }

    public Leader(TruckCore core, Platoon platoon) throws IOException {
        super(core);
        this.platoon = platoon;
    }

    public void broadcast(SocketAddress[] truckSocketAddresses, String messageType, String... args){
        String[] fullArgs = new String[args.length + 4];
        int counter = 0;
        for (String s : args)
            fullArgs[counter++] = s;
        fullArgs[counter] = "speed";
        fullArgs[counter + 1] = String.valueOf(this.getSpeed());
        fullArgs[counter + 2] = "truck_location";
        fullArgs[counter + 3] = this.getTailDirectionLocation().toString();
        for (SocketAddress truckAddress : truckSocketAddresses) {
            if (truckAddress != this.getSocketAddress()){
                this.core.sendMessageTo(truckAddress, messageType, -1, fullArgs);
            }
        }
    }
    
    public void removeFollower(Truck truck){
        throw new UnsupportedOperationException("Unimplemented method");
    }
    
    public void addFollower(Truck truck){
        throw new UnsupportedOperationException("Unimplemented method");
    }
    
    public void startPlatoon(){
        throw new UnsupportedOperationException("Unimplemented method");
    };
    
    public void discover(){
        throw new UnsupportedOperationException("Unimplemented method");
    };

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Unimplemented method");
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
                SocketAddress senderAddress = SocketAddress.fromString(messageBody.get("address"));
                int senderId = Integer.valueOf(messageBody.get("truck_id"));
                switch (messageType) {
                    case "join":
                        PotentialFollowerInfo newTruckInfo = new PotentialFollowerInfo(senderId, TruckLocation.fromString(messageBody.get("location")), senderAddress, message);
                        this.assignRoleToNewTruck(newTruckInfo);
                        break;
                    case "acknowledge_role":
                        PotentialFollowerInfo acknowledgedFollower = null;
                        for (PotentialFollowerInfo potentialFollowerInfo : joinedTrucksList) {
                            if (potentialFollowerInfo.id == senderId){
                                acknowledgedFollower = potentialFollowerInfo;
                            }
                        }
                        joinedTrucksList.remove(acknowledgedFollower);
                        // If all trucks acknowledged role, begin journey
                        if (joinedTrucksList.size() == 0){
                            truckState = "journey";
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void assignRoleToNewTruck(PotentialFollowerInfo newTruckInfo) {
        joinedTrucksList.add(newTruckInfo);
        if (truckState != "journey" && joinedTrucksList.size() < 3){
            this.logger.info("Not enough trucks in platoon. Won't assign role to new truck until more arrive.");
        } else if (truckState == "journey") {

        } else {
            // sort joined trucks based on distance to leader
            joinedTrucksList.sort(
                (t1, t2) ->
                (int) (GridMap.calculateDistance(this.getLocation(), t1.location.getHeadLocation()) - GridMap.calculateDistance(this.getLocation(), t2.location.getHeadLocation())));
            PotentialFollowerInfo primeFollower = joinedTrucksList.remove(0);
            
            // closest truck is prime follower
            this.platoon = new Platoon(
                new PlatoonTruckInfo(this.getTruckId(), this.getSocketAddress()),
                new PlatoonTruckInfo(primeFollower.id, primeFollower.address),
                new PlatoonTruckInfo(joinedTrucksList.get(0).id, joinedTrucksList.get(0).address),
                new PlatoonTruckInfo(joinedTrucksList.get(1).id, joinedTrucksList.get(1).address)
            );
            int optimalDistanceToLeaderTail = DISTANCE_BETWEEN_TRUCKS;
            try {
                this.sendMessageTo(
                    primeFollower.address,
                    "role",
                    Integer.valueOf(primeFollower.originatingMessage.getBody().get("truck_id")),
                    "role",
                    "prime_follower",
                    "platoon",
                    platoon.toJson(),
                    "speed",
                    String.valueOf(this.getSpeed()),
                    "truck_location",
                    String.valueOf(this.getTailDirectionLocation()),
                    "optimal_distance",
                    String.valueOf(optimalDistanceToLeaderTail));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            
            for (PotentialFollowerInfo potentialFollowerInfo : joinedTrucksList) {
                // Optimal distance = prime follower tail + between distance
                optimalDistanceToLeaderTail += 5;
                this.sendMessageTo(
                    potentialFollowerInfo.address,
                    "role",
                    Integer.valueOf(potentialFollowerInfo.originatingMessage.getBody().get("truck_id")),
                    "speed",
                    String.valueOf(this.getSpeed()),
                    "truck_location",
                    String.valueOf(this.getTailDirectionLocation()),
                    "role",
                    "follower",
                    "optimal_distance",
                    String.valueOf(optimalDistanceToLeaderTail));
                // Need to add follower 1 to distance
            }
            this.logger.info("Assigned roles to potential platoon trucks. Awaiting acknowledgement.");
        }
    }

    
    private TruckLocation getTailDirectionLocation() {
        Location currentLocation = this.getLocation();
       return new TruckLocation(currentLocation.getRow() + 2, currentLocation.getColumn(), getDirection());
    }

    private SocketAddress getSocketAddress() {
        return core.getSocketAddress();
    }

    @Override
    public void run() {
        logger.info("Beginning operation.");

        if (this.platoon == null){
            // Send discovery message to all trucks
            this.broadcast(otherTrucksSocketAddresses, "discovery");
        }

        truckState = "discovery";
        LocalDateTime waitTimer= Utils.nowDateTime();
        while (true) {
            processReceivedMessages();
            switch (truckState) {
                case "discovery":
                    // Wait 5 seconds for other trucks to join
                    if (ChronoUnit.SECONDS.between(waitTimer, Utils.nowDateTime()) >= 5 && joinedTrucksList.size() != 3) {
                        logger.info("Discovery unsuccessful. Trucked joined: " + joinedTrucksList.size());
                        truckState = "roaming";
                    // All trucks join. Start the journey
                    } else {
                        // this.logger.info("Waiting for trucks to join...");
                    }
                    break;          
                case "journey":
                    // Ping platoon trucks if it has been 5 seconds since last message exchange    
                    if (ChronoUnit.SECONDS.between(this.core.getTimeOfLastMessage(), Utils.nowDateTime()) >= WAIT_BEFORE_PING){
                        this.logger.info("Pinging followers");
                        this.broadcast(platoon.getSocketAddresses(), "ping");
                    }

                    Location currentLocation = this.getLocation();

                    // If not aligned with destination, change direction
                    if (currentLocation.getColumn() != this.getDestination().getColumn()){
                        int targetColumn = this.getDestination().getColumn();
                        int columnDifference = currentLocation.getColumn() - targetColumn;
                        boolean needToBroadcastChange = false;
                        if (columnDifference > 0 && this.getDirection() != Direction.NORTH_WEST){
                            this.changeDirection(Direction.NORTH_WEST);
                            needToBroadcastChange = true;
                        } else if (columnDifference < 0 && this.getDirection() != Direction.NORTH_EAST){
                            this.changeDirection(Direction.NORTH_EAST);
                            needToBroadcastChange = true;
                        }
                        if (needToBroadcastChange){
                            this.broadcast(platoon.getSocketAddresses(), "new_direction", "target_column", String.valueOf(targetColumn));
                        }
                        this.logger.info("Changing direction to" + this.getDirection());
                    } else if (this.getDirection() != Direction.NORTH) {
                        this.changeDirection(Direction.NORTH);
                    }


                    // Handle speed
                    if (currentLocation.getRow() != this.getDestination().getRow()){ // Not at destination yet
                        int newSpeed = this.getSpeed();
                        boolean hasSpeedChanged = false;
                        if (currentLocation.getRow() - this.getDestination().getRow() >= 2 && this.getSpeed() != JOURNEY_SPEED){
                           newSpeed = JOURNEY_SPEED;
                            hasSpeedChanged = true;
                        }

                        // Check to see if we need to slow down to arrive in the desired row
                        int slowerSpeed = newSpeed;
                        while (slowerSpeed > 0) {
                            slowerSpeed--;
                            if(currentLocation.getRow() - slowerSpeed == this.getDestination().getRow()){
                                newSpeed = slowerSpeed;
                                hasSpeedChanged = true;
                                this.logger.info("Slowing speed at " + currentLocation.getRow() + " to " + newSpeed + " to arrive at destination " + this.getDestination().getRow());
                                break;
                            }
                        }
                        if (hasSpeedChanged){
                            this.changeSpeed(newSpeed);
                        }
                    } else { // Arrived at destination
                        this.logger.info("Arrived at destination.");
                        this.changeSpeed(0);
                        this.broadcast(platoon.getSocketAddresses(), "journey_end");
                        truckState = "journey_end";
                    }

                    this.logger.info("Moving to destination at speed " + this.getSpeed());
                    break;
                case "journey_end":
                    this.logger.info("Journey ended. Sleeping.");
                    break;
                default:
                    this.logger.severe("Truck in unknown truckState: " + truckState);
                    break;
            }
        }
    }

    @Override
    protected void changeDirection(Direction newDirection) {
        super.changeDirection(newDirection);
    }

    @Override
    public void changeSpeed(int newSpeed) {
        this.logger.info("Changing speed to " + newSpeed);
        this.core.setSpeed(newSpeed);
        this.broadcast(
            platoon.getSocketAddresses(),
            "new_speed"
            );
    }

    public void handleUnresponsiveReceiver(Message message) {
        throw new UnsupportedOperationException("Unimplemented method 'handleUnresponsiveReceiver'");
    }
}

