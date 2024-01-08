package src;
//public class Leader extends PlatoonTruck {
//    private Platoon platoon;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//
//public class Leader extends PlatoonTruck {
//        private final Set<Truck> followers;
//        private volatile boolean isPlatooning;
//
//        public Leader(String ipAddress, int port) {
//            super(ipAddress, port);
//            this.followers = Collections.synchronizedSet(new HashSet<>());
//            this.isPlatooning = false;
//        }
//
//        // Other methods remain the same
//    
//
//
//    public void broadcast(Message message) {
//        // Implementation
//    }
//
//    public void removeFollower(Truck follower) {
//        // Implementation
//    }
//
//    public void addFollower(Truck follower) {
//        // Implementation
//    }
//
//    public void startPlatoon() {
//        // Implementation
//    }
//
//    public void stopPlatoon() {
//        // Implementation
//    }
//
//    public void discover() {
//        // Implementation for discovering followers
//    }

    // Constructor, getters, setters
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.concurrent.TimeUnit;

    public class Leader extends PlatoonTruck {

        private ArrayList<Foreigner> trucks;
        private String state;
        private String joinedTrucks;

        public Leader(int id, String direction, float speed, GPSLocation destination, GPSLocation location, ArrayList<Foreigner> trucks) {
            super(id, direction, speed, destination, location);
            this.trucks = trucks;
        }

        public void broadcast(Message message) {
            // Implementation for broadcasting a message
            // System.out.println("Leader broadcasting: " + message.getBody());
        }

        public void removeFollower(PlatoonTruck truck) {
            // Implementation for removing a follower
        }

        public void addFollower(Foreigner truck) {
            // Implementation for adding a foreigner as a follower
        }

        public void startPlatoon() {
            // Implementation for starting the platoon
        }

        public void discover() {
            // Implementation for discovering information
        }

        @Override
        public void run() {
            System.out.println("\nBeginning operator");
            int waitForJoin = 0;
            state = "discovery";
            while (true) {
                processReceivedMessages();
                if ("discovery".equals(state)) {
                    if (waitForJoin == 5 && joinedTrucks.length() != 3) {
                        System.out.println("Discovery unsuccessful. Trucks joined: " + joinedTrucks.length());
                    } else if (waitForJoin == 5 && joinedTrucks.length() == 3) {
                        System.out.println("Discovery successful. Starting journey...");
                        state = "journey";
                    }
                    Map<String, String> messageBody = new HashMap<>();
                    messageBody.put("utc", now());
                    messageBody.put("truckID", String.valueOf(getId()));
                    messageBody.put("address", "localhost:port");
                    Message message = new Message(incrementAndGetMessageCounter(), "discovery", messageBody);
                    broadcast(message);
                    waitForJoin++;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void setPlatoon(Platoon platoon) {
            // Implementation for setting the platoon
        }

        @Override
        public Platoon getPlatoon() {
            return super.getPlatoon();
        }
    }


