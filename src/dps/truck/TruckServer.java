package dps.truck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonProcessingException;

import dps.Message;

public class TruckServer implements Runnable {
    private SocketAddress socketAddress;
    private Truck truck;

    public TruckServer(SocketAddress socketAddress, Truck truck) throws IOException {
        this.socketAddress = socketAddress;
        this.truck = truck;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(this.socketAddress.getPort())) {
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String receivedMessage = in.readLine();
                    truck.addMessageToQueue(Message.fromJson(receivedMessage));
                
                } catch (JsonProcessingException e) {
                    System.out.println(e.getStackTrace().toString());
                } catch (IOException e) {
                    System.err.println("Error in communication with the client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server on port " + this.socketAddress.toString() + ": " + e.getMessage());
        }
    }
}
