package dps.platoon;

import dps.GPSLocation;
import dps.Message;
import dps.Truck;

public class Follower extends Truck {

    Leader leader;

    public Follower(int id, String direction, float speed, GPSLocation destination, GPSLocation location, Leader leader) {
        super(id, direction, speed, destination, location);
        this.leader = leader;
    }

    public void sendMessageToLeader(Message m){
        throw new UnsupportedOperationException("Unimplemented method");
    };
    
    public void disconnect(){
        throw new UnsupportedOperationException("Unimplemented method");
    };

    @Override
    public void run() {
        throw new UnsupportedOperationException("Unimplemented method");
    }
}
