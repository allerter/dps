package src;

public class Truck {
    private String direction;
    private double speed;
    private double maintainDistance;
   // private GPSLocation destination;
    //private GPSLocation location;
    private boolean collisionSensor;

    private String ipAddress;
    private int port;
    

    public Truck(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }


	public String getDirection() {
		return direction;
	}


	public void setDirection(String direction) {
		this.direction = direction;
	}


	public double getSpeed() {
		return speed;
	}


	public void setSpeed(double speed) {
		this.speed = speed;
	}


	public double getMaintainDistance() {
		return maintainDistance;
	}


	public void setMaintainDistance(double maintainDistance) {
		this.maintainDistance = maintainDistance;
	}


	public boolean isCollisionSensor() {
		return collisionSensor;
	}


	public void setCollisionSensor(boolean collisionSensor) {
		this.collisionSensor = collisionSensor;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}
    

    // Getters and setters for all fields
    // Additional methods like changeSpeed, changeDirection, etc.
}
