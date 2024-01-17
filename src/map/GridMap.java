package map;
import java.util.logging.Logger;

import dps.truck.TruckLocation;


public class GridMap {
    
    private String[][] grid;
    private Logger logger;

    // Function to initialize the grid with values
    public GridMap(
        int rows,
        int columns) {
        
        // Set up logger
        this.logger = Logger.getLogger(this.getClass().getSimpleName());

        // Fill grid with empty
        this.grid = new String[rows][columns];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = "-";
            }
        }

    }

    // Function to print the grid map
    public void printGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Function to add a new thing to the grid
    public void addTruck(int truck_id, TruckLocation truckLocation) {
        // Check if the specified location is empty before adding the new thing

        int head_row = truckLocation.getRow();
        int head_column = truckLocation.getColumn();
        Direction direction = truckLocation.getDirection();

        // Add truck to map based on direction and head location
        if (isValidLocation(head_row, head_column)) {
            grid[head_row][head_column] = "Head" + truck_id;
            if (direction == Direction.NORTH){
                grid[head_row][head_column + 1] = "Tail" + truck_id;
                grid[head_row][head_column + 1] = "Tail" + truck_id;
            } else if (direction == Direction.EAST) {
                grid[head_row - 1][head_column] = "Tail" + truck_id;
                grid[head_row - 2][head_column] = "Tail" + truck_id;
            } else if (direction == Direction.WEST) {
                grid[head_row + 1][head_column] = "Tail" + truck_id;
                grid[head_row + 2][head_column] = "Tail" + truck_id;
            } else {
                throw new IllegalArgumentException("Invalid direction for new truck: " + direction);
            }
        } else {
            this.logger.info("Truck location is not valid: " + head_row + ":" + head_column);
        }
    }

    // Function to check if a location is valid (empty) in the grid
    boolean isValidLocation(int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[1].length && grid[row][col] == "-";
    }

    public static int calculateDistance(Location location, Location location2) {
        return (location.getRow() - location2.getRow()) + (location.getColumn() - location2.getColumn());
    }
}