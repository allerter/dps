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
import dps.truck.TruckLocation;
import dps.truck.TruckServer;
import map.GridMap;
import map.Location;
import map.Direction;

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

        // Set up map
        Location destination = new Location(0, 50);
        GridMap map = new GridMap(100, 100);
        TruckLocation leaderLocation = new TruckLocation(97, 50, Direction.NORTH);
        TruckLocation[] otherTruckLocations= {
            new TruckLocation(97, 48, Direction.NORTH),
            new TruckLocation(97, 46, Direction.NORTH),
            new TruckLocation(97, 52, Direction.NORTH)
        };
        // Add trucks to map
        map.addTruck(0, leaderLocation);
        for (int i = 1; i <= otherTruckLocations.length; i++) {
            map.addTruck(i, otherTruckLocations[i - 1]);
        }


        // initialize platoon:
        // 1 Leader, 1 Prime Follower, 2 Followers
        TruckServer[] truckCores = new TruckServer[3];
        try {

            truckCores[0] = new TruckServer(1, 0.0, otherTruckLocations[0], destination, new SocketAddress("127.0.0.1", 5001), map);
            truckCores[1] = new TruckServer(2, 0.0, otherTruckLocations[1], destination, new SocketAddress("127.0.0.1", 5002), map);
            truckCores[2] = new TruckServer(3, 0.0, otherTruckLocations[2], destination, new SocketAddress("127.0.0.1", 5003), map);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            truckCores[0].setTruck(new Truck(truckCores[0]));
            truckCores[1].setTruck(new Truck(truckCores[1]));
            truckCores[2].setTruck(new Truck(truckCores[2]));

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
            TruckServer leaderCore = new TruckServer(0, 0.0, leaderLocation, destination, new SocketAddress("127.0.0.1", 5000), map);
            leaderCore.setTruck(new Leader(
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

    while (true) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // map.printGrid();
    }
    }
}
