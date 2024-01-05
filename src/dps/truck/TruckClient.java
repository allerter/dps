package dps.truck;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import dps.Message;

public class TruckClient {
    public static void sendMessage(String ipAddress, int port, Message message) throws IOException {
        try (Socket socket = new Socket(ipAddress, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message.toJson());
        } catch (JsonProcessingException e) {
            System.out.println(e.getStackTrace().toString());
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ipAddress);
        }
        Logger.getLogger("comms").fine("Sent message to " + ipAddress + ":" + port);
    }
}
