package dps.truck;

import java.util.concurrent.TimeUnit;

import java.util.Random;
public class TruckInMotion {
    // Placeholder data fields, replace with your actual data
    private double[][] distanceMatrix;
    private double[] speeds;
    private CollisionState collisionState;

    // Enumeration for collision states
    public enum CollisionState {
        SAFE,
        WARNING,
        DANGER,
        COLLIDED
    }

    // Singleton pattern to ensure a single shared instance
    private static volatile TruckInMotion sharedInstance;

    public TruckInMotion() {
        // Initialize the shared instance with placeholder data
        distanceMatrix = new double[][]{{0.0, 10.0, 20.0}, {10.0, 0.0, 15.0}, {20.0, 15.0, 0.0}};
        speeds = new double[]{50.0, 60.0, 70.0};
        collisionState = CollisionState.SAFE; // Initial state
    }

    public static synchronized TruckInMotion getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new TruckInMotion();
        }
        return sharedInstance;
    }

    public void runSimulation() {
    	for (int iteration = 1; iteration <= 15; iteration++) {
        // Placeholder methods, replace with your actual logic
        simulateDynamicUpdates();
        applySafetyConstraints();
        varyCollisionStates(iteration);
        visualize();
        addDelay();
     }
    }	

    // Placeholder methods, replace with your actual logic
    private void simulateDynamicUpdates() {
        // Simulate dynamic updates
        Random random = new Random();
        for (int i = 0; i < speeds.length; i++) {
            speeds[i] += random.nextDouble() * 5.0 - 2.5; // Adjust speed randomly
        }
    }

    private void applySafetyConstraints() {
        // Apply safety constraints logic
        for (int i = 0; i < speeds.length; i++) {
            if (speeds[i] > 80.0) {
                speeds[i] = 80.0; // Limit speed to 80.0
            }
        }
    }

    private void varyCollisionStates(int iteration) {
        // Vary collision states based on the iteration number
        if (iteration >= 1 && iteration <= 5) {
            updateCollisionState(CollisionState.SAFE);
        } else if (iteration >= 6 && iteration <= 10) {
            updateCollisionState(CollisionState.WARNING);
        } else {
            updateCollisionState(CollisionState.DANGER);
        }
    }

    private void visualize() {
        // Visualization logic
        System.out.println("Distances:");
        for (double[] row : distanceMatrix) {
            for (double distance : row) {
                System.out.print(distance + " ");
            }
            System.out.println();
        }
        System.out.println("Speeds:");
        for (double speed : speeds) {
            System.out.print(speed + " ");
        }
        System.out.println();
        System.out.println("Collision State: " + collisionState);
    }

    private void addDelay() {
        // Add a 2-second delay between iterations
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateSharedTruckMotion(TruckInMotion newTruckMotion) {
        // Update the current instance with data from newTruckMotion
        // (implement logic to merge or replace data as needed)
        // For simplicity, let's assume that all data should be replaced
        this.distanceMatrix = newTruckMotion.distanceMatrix;
        this.speeds = newTruckMotion.speeds;
        this.collisionState = newTruckMotion.collisionState;
    }

    private void updateCollisionState(CollisionState newState) {
        // Update the collision state
        this.collisionState = newState;
    }

	 
}
