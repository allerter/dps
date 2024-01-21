package map;

public class TruckInfo{
    int id;
    Direction direction;
    int speed;
    public TruckInfo(int id, Direction direction, int speed) {
        this.id = id;
        this.direction = direction;
        this.speed = speed;
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
}