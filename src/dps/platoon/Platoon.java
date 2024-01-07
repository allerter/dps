package dps.platoon;

public class Platoon {
    private Leader leader;
    private PrimeFollower primeFollower;
    private Follower[] followers;
    public Platoon(Leader leader, PrimeFollower primeFollower, Follower[] followers) {
        this.leader = leader;
        this.primeFollower = primeFollower;
        this.followers = followers;
    }

    public Leader getLeader() {
        return leader;
    }
    public void setLeader(Leader leader) {
        this.leader = leader;
    }
    public PrimeFollower getPrimeFollower() {
        return primeFollower;
    }
    public void setPrimeFollower(PrimeFollower primeFollower) {
        this.primeFollower = primeFollower;
    }
    public Follower[] getFollowers() {
        return followers;
    }
    public void setFollowers(Follower[] followers) {
        this.followers = followers;
    }
}
