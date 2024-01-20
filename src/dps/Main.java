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
import dps.truck.TruckServer;

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
        TruckServer[] truckCores = new TruckServer[3];
        try {

            truckCores[0] = new TruckServer(new SocketAddress("127.0.0.1", 5001), null);
            truckCores[1] = new TruckServer(new SocketAddress("127.0.0.1", 5002), null);
            truckCores[2] = new TruckServer(new SocketAddress("127.0.0.1", 5003), null);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {

            truckCores[0].setTruck(new Truck(1, null, "straight", 0, new GPSLocation(50.110924, 8.682127),
                    new GPSLocation(50.5136, 7.4653), truckCores[0]));
            truckCores[1].setTruck(new Truck(2, null, "straight", 0, new GPSLocation(50.110924, 8.682127),
                    new GPSLocation(49.5136, 7.4653),
                    truckCores[1]));
            truckCores[2].setTruck(new Truck(3, null, "straight", 0, new GPSLocation(50.110924, 8.682127),
                    new GPSLocation(48.5136, 7.4653),
                    truckCores[2]));

            for (TruckServer truckCore : truckCores) {
                truckCore.start();
                logger.info("Deployed Truck " + truckCore.getTruck().getTruckId());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            SocketAddress[] trucksAddresses = new SocketAddress[truckCores.length];
            for (int i = 0; i < truckCores.length; i++) {
                trucksAddresses[i] = truckCores[i].getSocketAddress();
            }
            TruckServer leaderCore = new TruckServer(new SocketAddress("127.0.0.1", 5000), null);
            leaderCore.setTruck(new Leader(
                    0,
                    null, "straight",
                    0.0,
                    new GPSLocation(50.110924, 8.682127),
                    new GPSLocation(51.5136, 7.4653),
                    leaderCore,
                    trucksAddresses));
            leaderCore.start();
            logger.info("Deployed  Leader");
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        for (TruckServer truck : truckCores) {
            try {
                truck.join();
            } catch (InterruptedException e) {
                logger.severe(e.getStackTrace().toString());
            }
        }
    }

}
