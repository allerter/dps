package dps;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import dps.platoon.Leader;

public class Main {

    static {
        // Set up logging
        Logger globalLogger = Logger.getGlobal();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        System.setProperty("format", "[%1$tF %1$tT] [%4$-7s] [%3$-22s] %5$s %n");
        globalLogger.addHandler(consoleHandler);
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("main");

        // initialize platoon:
        // 1 Leader, 1 Prime Follower, 2 Followers
        System.out.println("beginning");
        Truck[] trucks = {
                new Truck(1, "straight", 0, new GPSLocation(50.110924, 8.682127), new GPSLocation(50.5136, 7.4653)),
                new Truck(2, "straight", 0, new GPSLocation(50.110924, 8.682127), new GPSLocation(49.5136, 7.4653)),
                new Truck(3, "straight", 0, new GPSLocation(50.110924, 8.682127), new GPSLocation(48.5136, 7.4653)),
        };
        for (Truck truck : trucks) {
            truck.start();
            logger.info("Deployed Truck " + truck.getId());
        }

        Leader leader = new Leader(
                0,
                "straight",
                0,
                new GPSLocation(50.110924, 8.682127),
                new GPSLocation(51.5136, 7.4653),
                trucks);
        leader.start();
        logger.info("Deployed  Leader");

        for (Truck truck : trucks) {
            try {
                truck.join();
            } catch (InterruptedException e) {
                logger.severe(e.getStackTrace().toString());
            }
        }
    }

}
