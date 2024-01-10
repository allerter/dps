package dps;
public class GPSLocation {
    double latitude;
    double longitude;

    public GPSLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("%f,%f", this.latitude, this.longitude);
    }

    public static GPSLocation fromString(String s) {
        String[] location = s.split(",");
        return new GPSLocation(Double.valueOf(location[0]), Double.valueOf(location[1]));
    }
}