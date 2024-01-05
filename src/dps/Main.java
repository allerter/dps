package dps;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import dps.platoon.Leader;
import dps.truck.SocketAddress;
import dps.truck.Truck;

public class Main {

    static {
        // Set up logging
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] [%3$-22s] %5$s %n");        
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());

        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.FINEST);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.FINEST);
        }
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("main");

        // initialize platoon:
        // 1 Leader, 1 Prime Follower, 2 Followers
        Truck[] trucks = new Truck[3];
        try {

            trucks[0] = new Truck(1, "straight", 0, new GPSLocation(50.110924, 8.682127),
                    new GPSLocation(50.5136, 7.4653), new SocketAddress("127.0.0.1", 5001));
            trucks[1] = new Truck(2, "straight", 0, new GPSLocation(50.110924, 8.682127),
                    new GPSLocation(49.5136, 7.4653),
                    new SocketAddress("127.0.0.1", 5002));
            trucks[2] = new Truck(3, "straight", 0, new GPSLocation(50.110924, 8.682127),
                    new GPSLocation(48.5136, 7.4653),
                    new SocketAddress("127.0.0.1", 5003));

            for (Truck truck : trucks) {
                truck.start();
                logger.info("Deployed Truck " + truck.getId());
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return;
        }

        Leader leader;
        try {
            SocketAddress[] trucksAddresses = new SocketAddress[trucks.length];
            for (int i=0; i < trucks.length; i++) {
                trucksAddresses[i] = trucks[i].getSocketAddress();
            }
            leader = new Leader(
                    0,
                    "straight",
                    0,
                    new GPSLocation(50.110924, 8.682127),
                    new GPSLocation(51.5136, 7.4653),
                    new SocketAddress("127.0.0.1", 5000),
                    trucksAddresses);
            leader.start();
            logger.info("Deployed  Leader");
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        for (Truck truck : trucks) {
            try {
                truck.join();
            } catch (InterruptedException e) {
                logger.severe(e.getStackTrace().toString());
            }
        }
    }

}
