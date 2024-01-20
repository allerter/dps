package dps.platoon;

import java.io.IOException;

import dps.GPSLocation;
import dps.Message;
import dps.truck.Truck;
import dps.truck.TruckInMotion;
import dps.truck.TruckServer;

public class PrimeFollower extends Truck implements PlatoonTruck {

    private Platoon platoon; 

    public PrimeFollower(int id,TruckInMotion sharedTruckMotion , String direction, double speed, GPSLocation destination, GPSLocation location, TruckServer server, Platoon platoon) throws IOException {
        super(id,sharedTruckMotion, direction, speed, destination, location, server);
        this.platoon = platoon;
    }

    public void replaceLeader(){
        throw new UnsupportedOperationException("Unimplemented method");
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
    }

    @Override
    public void discover() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'discover'");
    }

    public void handleUnresponsiveReceiver(Message message) {
        throw new UnsupportedOperationException("Unimplemented method 'handleUnresponsiveReceiver'");
    }
}
