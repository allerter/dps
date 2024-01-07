package src;
import java.util.*;
import java.io.IOException;
//
//public class PlatoonTruck extends Truck {
//	private TruckServer server;
//
//	public PlatoonTruck(String ipAddress, int port) {
//	    super(ipAddress, port);
//	    this.server = new TruckServer(this);
//	    new Thread(server).start();
//	  }
//
//	    public void sendMessageTo(String message, String receiverIp, int receiverPort) {
//	        TruckClient.sendMessage(receiverIp, receiverPort, message);
//	    }
//
//	    // Additional methods...
//	
//
//	
//	
//    // Additional attributes if needed
//
//    public void sendMessage(Message message) {
//        // Implementation
//    }
//
//    public void processReceivedMessage(Message message) {
//        // Implementation
//    }
//
//    public void disconnect() {
//        // Implementation
//    }
//
//    // Additional methods as needed
//}
//
//
//
public class PlatoonTruck extends Truck {
    private TruckServer server;

    public PlatoonTruck(String ipAddress, int port) {
        super(ipAddress, port);
        try {
            this.server = new TruckServer(port, this);
            new Thread(server).start();
        } catch (IOException e) {
            System.err.println("Error starting TruckServer: " + e.getMessage());
        }
    }

    public void sendMessageTo(String message, String receiverIp, int receiverPort) {
        try {
            TruckClient.sendMessage(receiverIp, receiverPort, message);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public void processReceivedMessage(String message) {
        // Implement message processing logic
    }
}

