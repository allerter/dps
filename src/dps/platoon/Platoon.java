package dps.platoon;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import dps.truck.SocketAddress;

@JsonDeserialize(using = PlatoonDeserializer.class)
public class Platoon {
    PlatoonTruckInfo leader;
    PlatoonTruckInfo primeFollower;
    PlatoonTruckInfo follower1;
    PlatoonTruckInfo follower2;

    public Platoon(PlatoonTruckInfo leader, PlatoonTruckInfo primeFollower, PlatoonTruckInfo follower1,
            PlatoonTruckInfo follower2) {
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

    public static Platoon fromJson(String string) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(string, Platoon.class);
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @JsonIgnore
    public SocketAddress[] getSocketAddresses() {
        ArrayList<SocketAddress> socketAddresses = new ArrayList<>();
        socketAddresses.add(leader.socketAddress);
        socketAddresses.add(primeFollower.socketAddress);
        socketAddresses.add(follower1.socketAddress);
        socketAddresses.add(follower2.socketAddress);
        return (SocketAddress[]) socketAddresses.toArray();
    }

    @Override
    public String toString() {
        return String.format("Platoon(leader=%s, primeFollower=%s, follower1=%s, follower2=%s)",
        leader.toString(),
        primeFollower.toString(),
        follower1.toString(),
        follower2.toString());
    }

}

class PlatoonTruckInfo {
    int id;
    SocketAddress socketAddress;

    public PlatoonTruckInfo(int truckId, SocketAddress socketAddress) {
        this.id = truckId;
        this.socketAddress = socketAddress;
    }

    public int getId() {
        return id;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    @Override
    public String toString() {
        return String.format("PlatoonTruckInfo(id=%s, address=%s)", this.id, this.socketAddress.toString());
    }

}

class PlatoonDeserializer extends StdDeserializer<Platoon> {

    public PlatoonDeserializer() {
        this(null);
    }

    public PlatoonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Platoon deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode leaderNode = node.get("leader");
        JsonNode primeFollowerNode = node.get("primeFollower");
        JsonNode follower1Node = node.get("follower1");
        JsonNode follower2Node = node.get("follower2");
        return new Platoon(
                new PlatoonTruckInfo(leaderNode.get("id").asInt(),
                        new SocketAddress(leaderNode.get("socketAddress").get("ipAddress").asText(),
                                leaderNode.get("socketAddress").get("port").asInt())),
                new PlatoonTruckInfo(primeFollowerNode.get("id").asInt(),
                        new SocketAddress(primeFollowerNode.get("socketAddress").get("ipAddress").asText(),
                        primeFollowerNode.get("socketAddress").get("port").asInt())),
                new PlatoonTruckInfo(follower1Node.get("id").asInt(),
                        new SocketAddress(follower1Node.get("socketAddress").get("ipAddress").asText(),
                        follower1Node.get("socketAddress").get("port").asInt())),
                new PlatoonTruckInfo(follower2Node.get("id").asInt(),
                        new SocketAddress(follower2Node.get("socketAddress").get("ipAddress").asText(),
                        follower2Node.get("socketAddress").get("port").asInt())));
    }
}
