package dps.platoon;

import java.io.IOException;
import java.util.Map;
import java.util.Map;

import dps.GPSLocation;
import dps.Message;
import dps.Utils;
import dps.Utils;
import dps.truck.SocketAddress;
import dps.truck.Truck;
import dps.truck.TruckServer;
import dps.truck.TruckServer;

public class Follower extends Truck {

    SocketAddress leaderAddress;
    SocketAddress leaderAddress;

    public Follower(int id, String direction, double speed, GPSLocation destination, GPSLocation location, TruckServer server, SocketAddress leaderAddress) throws IOException {
        super(id, direction, speed, destination, location, server);
        this.leaderAddress = leaderAddress;
    }

    public void sendMessageToLeader(Message m) {
        throw new UnsupportedOperationException("Unimplemented method");
    };

    public void disconnect() {
        throw new UnsupportedOperationException("Unimplemented method");
    };

    @Override
    public void processReceivedMessages() {
        while (true) {
            Message message = this.server.getMessageQueue().poll();
            if (message == null) {
                return;
            } else {
                this.logger.info("New message available: " + message.toString());
                String messageType = message.getType();
                Map<String, String> messageBody = message.getBody();
                switch (messageType) {
                    case "discovery":
                        this.logger.info("Discovery message received. Discarding.");    
                        break;
                    case "speed_change":
                        this.changeSpeed(Integer.valueOf(messageBody.get("speed")));
                        this.logger.info("Adapted speed to leader's. New speed: " + this.getSpeed());
                    case "direction_change":
                        this.changeDirection(messageBody.get("direction"));
                        this.logger.info("Adapted speed to leader's. New speed: " + this.getSpeed());
                    case "leader_change":
                        
                    default:
                        this.logger.warning("Unknown message type: " + messageType + ". Ignoring.");
                    
                }
            }
        }
    }

    @Override
    public void run() {
        this.processReceivedMessages();
    }
}