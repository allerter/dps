package dps.platoon;

import java.io.IOException;

import dps.GPSLocation;
import dps.Message;
import dps.truck.Truck;

public class Follower extends Truck {

    Leader leader;

    public Follower(int id, String direction, float speed, GPSLocation destination, GPSLocation location, Leader leader, String ipAddress, int port) throws IOException {
        super(id, direction, speed, destination, location, ipAddress, port);
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
