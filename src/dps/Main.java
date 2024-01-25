package dps;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import dps.platoon.Leader;
import dps.truck.SocketAddress;
import dps.truck.Truck;
import dps.truck.TruckLocation;
import dps.truck.TruckCore;
import map.GridMap;
import map.Location;
import map.TruckInfo;
import map.Direction;

public class Main {

    static {
        // Set up logging
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] [%3$-22s] %5$s %n");
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());

        Logger rootLogger = Logger.getLogger("dps");
        rootLogger.setLevel(Level.FINEST);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.FINEST);
        }

        
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("dps.main");

        // Set up map
        Location destination = new Location(2, 25);
        GridMap map = new GridMap(50, 50);
        TruckLocation leaderLocation = new TruckLocation(31, 25, Direction.NORTH);
        TruckLocation[] otherTruckLocations= {
            new TruckLocation(36, 25, Direction.NORTH),
            new TruckLocation(41, 25, Direction.NORTH),
            new TruckLocation(46, 25, Direction.NORTH)
        };
        // Add trucks to map
        map.addTruck(0, leaderLocation);
        for (int i = 1; i <= otherTruckLocations.length; i++) {
            map.addTruck(i, otherTruckLocations[i - 1]);
        }


        // initialize platoon:
        // 1 Leader, 1 Prime Follower, 2 Followers
        ArrayList<TruckCore> truckCores = new ArrayList<>();
        try {

            truckCores.add(new TruckCore(1, 0, otherTruckLocations[0], destination, new SocketAddress("127.0.0.1", 5001), map));
            truckCores.add(new TruckCore(2, 0, otherTruckLocations[1], destination, new SocketAddress("127.0.0.1", 5002), map));
            truckCores.add(new TruckCore(3, 0, otherTruckLocations[2], destination, new SocketAddress("127.0.0.1", 5003), map));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            truckCores.get(0).setTruck(new Truck(truckCores.get(0)));
            truckCores.get(1).setTruck(new Truck(truckCores.get(1)));
            truckCores.get(2).setTruck(new Truck(truckCores.get(2)));

            for (TruckCore truckCore : truckCores) {
                truckCore.start();
                logger.info("Deployed Truck " + truckCore.getTruck().getTruckId());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        TruckCore leaderCore;
        try {
            SocketAddress[] trucksAddresses = new SocketAddress[truckCores.size()];
            for (int i = 0; i < truckCores.size(); i++) {
                trucksAddresses[i] = truckCores.get(i).getSocketAddress();
            }
            leaderCore = new TruckCore(0, 0, leaderLocation, destination, new SocketAddress("127.0.0.1", 5000), map);
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
        truckCores.add(0, leaderCore);

        LocalDateTime timer = Utils.nowDateTime();
        while (true) {
            // Create HashMaps and add entries for direction and speed
            List<TruckInfo> directionAndSpeedList = new ArrayList<>();
            for (TruckCore truckCore : truckCores) {
                Color color;
                Object role = truckCore.getTruck().getClass();
                if (role instanceof Leader){
                    if (truckCore.getTruck().getTruckState().equals("journey")){
                        color = Color.GREEN;
                    } else {
                        color = Color.GRAY;
                    }
                    
                } else if (role instanceof Truck){
                    color = Color.GRAY;
                } else {
                    color = Color.GREEN;
                }
                TruckInfo entry = new TruckInfo(truckCore.getTruckId(), truckCore.getDirection(), truckCore.getSpeed(), truckCore.getDestination(), color, truckCore.getClockMatrix());
                directionAndSpeedList.add(entry);
            }

            if (timer != null  && ChronoUnit.SECONDS.between(timer, Utils.nowDateTime()) >= 3){
                // destination.setColumn(24);
                Truck role = leaderCore.getRole();
                if (role instanceof Leader){
                    ((Leader)role).JOURNEY_SPEED = 4;
                    role.changeSpeed(4);
                }
                timer = null;
            }
            try {
                map.update(directionAndSpeedList.toArray(new TruckInfo[directionAndSpeedList.size()]));
                logger.finer("map updated");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // map.printGrid();
            // hi have a good day
        }
    }

}
