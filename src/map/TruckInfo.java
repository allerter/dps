package map;

import java.awt.Color;

public class TruckInfo{
    int id;
    Direction direction;
    int speed;
    Color color;
    int[] destination;
    public TruckInfo(int id, Direction direction, int speed, Location destination, Color color) {
        this.id = id;
        this.direction = direction;
        this.speed = speed;
        this.color = color;
        this.destination = new int[]{destination.getRow(), destination.getColumn()};
    }
    public int getId() {
        return id;
    }
    public Direction getDirection() {
        return direction;
    }
    public int getSpeed() {
        return speed;
    }
    public Color getColor() {
        return color;
    }
    public int[] getDestination() {
        return destination;
    }
}