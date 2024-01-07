package src;


import java.io.*;
import java.net.*;

public class TruckServer implements Runnable {
	private int port;
    private PlatoonTruck platoonTruck;

    public TruckServer(int port, PlatoonTruck platoonTruck) throws IOException {
        this.port = port;
        this.platoonTruck = platoonTruck;
    }
	
//    private Truck truck;
//
//    public TruckServer(Truck truck) {
//        this.truck = truck;
//    }

//    @Override
//    public void run() {
//        try (ServerSocket serverSocket = new ServerSocket(truck.getPort())) {
//            while (true) {
//                try (Socket clientSocket = serverSocket.accept();
//                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
//                    String inputLine;
//                    while ((inputLine = in.readLine()) != null) {
//                        System.out.println("Message received: " + inputLine);
//                        // process the message
//                        
//                    }
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("Exception caught when trying to listen on port " + truck.getPort());
//            System.out.println(e.getMessage());
//        }
//    }
//  
//}
//
//
//
//
//
//public class TruckServer implements Runnable {
//    private int port;
//    private PlatoonTruck platoonTruck;
//
//    public TruckServer(int port, PlatoonTruck platoonTruck) throws IOException {
//        this.port = port;
//        this.platoonTruck = platoonTruck;
//    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String receivedMessage = in.readLine();
                    platoonTruck.processReceivedMessage(receivedMessage);
                } catch (IOException e) {
                    System.err.println("Error in communication with the client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server on port " + port + ": " + e.getMessage());
        }
    }
}
