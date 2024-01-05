package dps.platoon;

import java.io.IOException;
import java.util.ArrayList;

import dps.GPSLocation;
import dps.Message;
import dps.truck.Truck;
import dps.Utils;

public class Leader extends Truck implements PlatoonTruck{

    private Platoon platoon;
    private Truck[] trucks;

    public Leader(int id, String direction, float speed, GPSLocation destination, GPSLocation location, String ipAddress, int port, Truck[] otherTrucks) throws IOException {
        super(id, direction, speed, destination, location, ipAddress, port);
        this.trucks = otherTrucks;
    }

    public Platoon getPlatoon() {
        return platoon;
    }

    public void setPlatoon(Platoon platoon) {
        this.platoon = platoon;
    }

    public void broadcast(){
        for (Truck truck : trucks) {
            this.logger.finer("Sending message to " + truck.getIpAddress() + ":" + truck.getPort());
            Message message = new Message(
                incrementAndGetMessageCounter(),
                Utils.now(),
                "discovery",
                "truck_id",
                Integer.toString(this.getTruckId()),
                "address",
                this.getIpAddress() + ":" + this.getPort()
            );
            this.sendMessageTo(message, truck.getIpAddress(), truck.getPort());
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

