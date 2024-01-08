package dps.platoon;

import java.util.ArrayList;

import dps.truck.SocketAddress;

public class Platoon {
    PlatoonTruckInfo leader;
    PlatoonTruckInfo primeFollower;
    PlatoonTruckInfo follower1;
    PlatoonTruckInfo follower2;
    public Platoon(PlatoonTruckInfo leader, PlatoonTruckInfo primeFollower, PlatoonTruckInfo follower1, PlatoonTruckInfo follower2) {
        this.leader = leader;
        this.primeFollower = primeFollower;
        this.follower1 = follower1;
        this.follower2 = follower2;
    }
    public PlatoonTruckInfo getLeader() {
        return leader;
    }
    public void setLeader(PlatoonTruckInfo leader) {
        this.leader = leader;
    }
    public PlatoonTruckInfo getPrimeFollower() {
        return primeFollower;
    }
    public void setPrimeFollower(PlatoonTruckInfo primeFollower) {
        this.primeFollower = primeFollower;
    }
    public PlatoonTruckInfo getFollower1() {
        return follower1;
    }
    public void setFollower1(PlatoonTruckInfo follower1) {
        this.follower1 = follower1;
    }
    public PlatoonTruckInfo getFollower2() {
        return follower2;
    }
    public void setFollower2(PlatoonTruckInfo follower2) {
        this.follower2 = follower2;
    }

    public static Platoon fromJson(String string) {
        throw new UnsupportedOperationException("Unimplemented method");
    }
 
    public SocketAddress[] getSocketAddresses() {
        ArrayList<SocketAddress> socketAddresses = new ArrayList<>();
        socketAddresses.add(leader.socketAddress);
        socketAddresses.add(primeFollower.socketAddress);
        socketAddresses.add(follower1.socketAddress);
        socketAddresses.add(follower2.socketAddress);
        return (SocketAddress[]) socketAddresses.toArray();
    }

}


class PlatoonTruckInfo {
    int id;
    SocketAddress socketAddress; 
}