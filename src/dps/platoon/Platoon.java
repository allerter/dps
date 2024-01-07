package dps.platoon;

import dps.truck.SocketAddress;

public class Platoon {
    PlatoonTruckInfo primeFollower;
    PlatoonTruckInfo follower1;
    PlatoonTruckInfo follower2;
    public Platoon(PlatoonTruckInfo primeFollower, PlatoonTruckInfo follower1, PlatoonTruckInfo follower2) {
        this.primeFollower = primeFollower;
        this.follower1 = follower1;
        this.follower2 = follower2;
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
    
}


class PlatoonTruckInfo {
    int id;
    SocketAddress socketAddress; 
}