package dps.truck;

import map.Direction;
import map.Location;

public class TruckLocation {
    private Location headLocation;
    private Direction direction;
    
    public TruckLocation(int headRow, int headColumn, Direction direction) {
        this.headLocation = new Location(headRow, headColumn);
        this.direction = direction;
    }
    public Direction getDirection() {
        return direction;
    }
    public int getRow(){
        return headLocation.getRow();
    }
    public void setRow(int row){
        headLocation.setRow(row);
    }
    public int getColumn(){
        return headLocation.getColumn();
    }
    public void setColumn(int column){
        headLocation.setColumn(column);
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Location getHeadLocation() {
        return headLocation;
    }
    public static TruckLocation fromString(String string) {
        String[] values = string.split(":");
        if (!(values.length == 3)){
            throw new IllegalArgumentException("Invalid string to form TruckLocation from: " + string);
        }
        return new TruckLocation(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Direction.valueOf(values[2]));
    }

    public String toString(){
        return String.format("%d:%d:%s", this.getRow(), this.getColumn(), this.getDirection().toString());
    }
}
