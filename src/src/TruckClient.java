package src;


import java.io.*;
import java.net.*;

public class TruckClient {
    public static void sendMessage(String ipAddress, int port, String message)throws IOException {
        try (Socket socket = new Socket(ipAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ipAddress);
        }
    }
}




