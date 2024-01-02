package dps.platoon;

import dps.GPSLocation;
import dps.Truck;

public class PrimeFollower extends Truck implements PlatoonTruck {

    private Platoon platoon; 

    public PrimeFollower(int id, String direction, float speed, GPSLocation destination, GPSLocation location, Platoon platoon) {
        super(id, direction, speed, destination, location);
        this.platoon = platoon;
    }

    public Platoon getPlatoon() {
        return platoon;
    }
    public void setPlatoon(Platoon platoon) {
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
}
