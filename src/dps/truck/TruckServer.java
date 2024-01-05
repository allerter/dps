package dps.truck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonProcessingException;

import dps.Message;

public class TruckServer implements Runnable {
    private int port;
    private Truck truck;

    public TruckServer(int port, Truck truck) throws IOException {
        this.port = port;
        this.truck = truck;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
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
            System.err.println("Error starting server on port " + port + ": " + e.getMessage());
        }
    }
}
