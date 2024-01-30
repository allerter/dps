package dps.truck;

import java.time.LocalDateTime;

import dps.Utils;
import map.Direction;

public class DatedTruckLocation extends TruckLocation {

    LocalDateTime dateTime;

    public DatedTruckLocation(int headRow, int headColumn, Direction direction) {
        super(headRow, headColumn, direction);

        this.dateTime = Utils.nowDateTime();
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public static DatedTruckLocation fromString(String string) {
        String[] values = string.split(":");
        if (!(values.length == 3)){
            throw new IllegalArgumentException("Invalid string to form TruckLocation from: " + string);
        }
        return new DatedTruckLocation(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Direction.valueOf(values[2]));
    }
    
}
