package map;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.JFrame;

import dps.truck.TruckLocation;

public class GridMap extends JFrame {

    private String[][] grid;
    private Logger logger;
    private TruckInfo[] truckInfos;

    public GridMap(int rows, int columns) {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("grid Grid Plotter");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE); // Set background color to white
        setVisible(true);

        // Set up logger
        this.logger = Logger.getLogger("dps." + this.getClass().getSimpleName());

        // Fill grid with empty
        this.grid = new String[rows][columns];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = "-";
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    
        int numRows = grid.length;
        int numCols = grid[0].length;
    
        int cellWidth = (getWidth() / numCols);
        int cellHeight = (getHeight() / numRows);
    
        // Draw white rectangles with black borders (grid)
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                String value = grid[i][j];
    
                Color color;
                // Set color based on the value
                if (value.startsWith("H")) {
                    if (truckInfos != null) {
                        TruckInfo truck = truckInfos[Character.getNumericValue(value.charAt(1))];
                        color = truck.color;
                    } else {
                        color = Color.GRAY;
                    }
                } else {
                    color = Color.WHITE;
                }
                g.setColor(color);
                g.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
    
                // Draw black border lines
                g.setColor(Color.BLACK);
                g.drawRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
    
                // Draw the value in the center
                g.drawString(String.valueOf(value), j * cellWidth + cellWidth / 2 - 5,
                        i * cellHeight + cellHeight / 2 + 5);
            }
        }
    
        // Draw four rectangles at the adjusted position on the right-hand side with borders and information
        int rectWidth = 150;
        int rectHeight = 60;
        int textPadding = 5;
    
        int rectX = getWidth() - rectWidth - 20;
        int rectY = (getHeight() - rectHeight * 4 - 30) / 2;
    
        // Loop to draw four rectangles
        for (int i = 0; i < 4; i++) {
            // Draw rectangles with borders
            g.setColor(Color.WHITE);
            g.fillRect(rectX, rectY + i * (rectHeight + 10), rectWidth, rectHeight);
            g.setColor(Color.BLACK);
            g.drawRect(rectX, rectY + i * (rectHeight + 10), rectWidth, rectHeight);
    
            int truckId;
            int speed;
            String direction;
            String destination;
            if (truckInfos != null){
                TruckInfo truck = truckInfos[i];
                truckId = truck.id;
                speed = truck.speed;
                direction = truck.direction.toString();
                destination = Arrays.toString(truck.destination);
            } else {
                truckId = i;
                speed = 0;
                direction = "-";
                destination = "-";
            }

            // Display information within each rectangle
            g.setColor(Color.BLACK);
            g.drawString("Truck: " + truckId, rectX + textPadding, rectY + i * (rectHeight + 10) + rectHeight / 5);
            g.drawString("Speed: " + speed, rectX + textPadding, rectY + i * (rectHeight + 10) + 2 * rectHeight / 5);
            g.drawString("Direction: " + direction, rectX + textPadding, rectY + i * (rectHeight + 10) + 3 * rectHeight / 5);
            g.drawString("Destination: " + destination, rectX + textPadding, rectY + i * (rectHeight + 10) + 4 * rectHeight / 5);
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
            grid[head_row][head_column] = "H" + truck_id;
            if (direction == Direction.NORTH){
                grid[head_row + 1][head_column] = "T" + truck_id;
                grid[head_row + 2][head_column] = "T" + truck_id;
            } else if (direction == Direction.EAST) {
                grid[head_row][head_column - 1] = "T" + truck_id;
                grid[head_row][head_column - 2] = "T" + truck_id;
            } else if (direction == Direction.WEST) {
                grid[head_row][head_column + 1] = "T" + truck_id;
                grid[head_row][head_column + 2] = "T" + truck_id;
            } else {
                throw new IllegalArgumentException("Invalid direction for new truck: " + direction);
            }
        } else {
            this.logger.info("Truck location is not valid: " + head_row + ":" + head_column);
        }
        repaint();
    }

    // Function to check if a location is valid (empty) in the grid
    boolean isValidLocation(int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[1].length && grid[row][col] == "-";
    }

    public static int calculateDistance(Location location, Location location2) {
        return (location2.getRow() - location.getRow()) + (location2.getColumn() - location.getColumn());
    }

    synchronized public void update(TruckInfo[] truckInfos) {
        boolean needToRepaint = false;
        this.truckInfos = truckInfos;
        for (TruckInfo truckInfo : truckInfos) {
            int id = truckInfo.getId();
            int speed = truckInfo.getSpeed();
            Direction direction = truckInfo.getDirection();
            // Move each truck to new position based on speed and direction
            if (speed > 0){
                int[] truckHead = findElement("H" + id);


                if (truckHead == null){
                    continue;
                }
                // If direction is north, trucks go straight up in grid
                // Otherwise, they will move 1 column to left or right
                int destCol;
                if (direction == Direction.NORTH){
                    destCol = truckHead[1];
                } else if (direction == Direction.NORTH_EAST){
                    destCol = truckHead[1] + 1;
                } else if (direction == Direction.NORTH_WEST){
                    destCol = truckHead[1] - 1;
                } else {
                    throw new IllegalArgumentException("Truck has illegal direction: " + direction);
                }
                moveTruck(truckHead[0], truckHead[1], truckHead[0] - speed, destCol);
                needToRepaint = true;
            }
        }
        if (needToRepaint){
            repaint();
        }
    }

    // Method to find the position of an element in a 2D grid
    public int[] findElement(String target) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].equals(target)) {
                    return new int[]{i, j}; // Return the position if the element is found
                }
            }
        }
        return null; // Return null if the element is not found
    }

    private boolean moveTruck(int sourceRow, int sourceCol, int destRow, int destCol) {
        // Move head
        if (sourceRow == destRow && sourceCol == destCol){
            return false;
        }
        grid[destRow][destCol] = grid[sourceRow][sourceCol];
        grid[sourceRow][sourceCol] = "-";

        // Move tail
        for (int i = 1; i <= 2; i++) {
            grid[destRow + i][destCol] = grid[sourceRow + i][sourceCol];
            grid[sourceRow + i][sourceCol] = "-";
        }
        return true;
    }
}