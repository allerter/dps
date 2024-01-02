package dps.platoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dps.GPSLocation;
import dps.Message;
import dps.Truck;
import dps.Utils;

public class Leader extends Truck implements PlatoonTruck{

    private Platoon platoon;
    private Truck[] trucks;

    public Leader(int id, String direction, float speed, GPSLocation destination, GPSLocation location, Truck[] otherTrucks) {
        super(id, direction, speed, destination, location);
        this.trucks = otherTrucks;
    }

    public Platoon getPlatoon() {
        return platoon;
    }

    public void setPlatoon(Platoon platoon) {
        this.platoon = platoon;
    }

    public void broadcast(Message message){
        throw new UnsupportedOperationException("Unimplemented method");
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
                    if (waitForJoin == 5 && joinedTrucks.size() != 3) {
                        logger.info("Discovery unsuccessful. Trucked joined: " + joinedTrucks.size());
                    } else if (joinedTrucks.size() == 3) {
                        logger.info("Discovery successful. Starting journey...");
                        truckState = "journey";
                    }
                    Map<String, String> messageBody = new HashMap<String, String>();
                    messageBody.put("utc", Utils.now());
                    messageBody.put("truck_id", Integer.toString(this.getTruckId()));
                    messageBody.put("address", "localhost:port");
                    Message message = new Message(incrementAndGetMessageCounter(), "discovery", messageBody);
                    broadcast(message);
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

