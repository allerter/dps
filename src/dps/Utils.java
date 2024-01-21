package dps;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

    public static String now() {
        return dtf.format(LocalDateTime.now());
    }

    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }

    public static LocalDateTime dateTimeFromString(String s){
        return LocalDateTime.parse(s, dtf);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * 
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     * from https://stackoverflow.com/q/3694380/4249434
     */
    public static double distance(GPSLocation loc1, GPSLocation loc2) {

        final int R = 6371; // Radius of the earth

        double lat1 = loc1.getLatitude();
        double lon1 = loc1.getLongitude();
        double el1 = 0.0;

        double lat2 = loc2.getLatitude();
        double lon2 = loc2.getLongitude();
        double el2 = 0.0;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static int[] stringToClockMatrixArray(String s) {
        // Split the string using the colon delimiter
        String[] parts = s.split(":");
        
        // Initialize an array to store integers
        int[] intArray = new int[parts.length];
        
        // Parse each substring into an integer and store in the array
        for (int i = 0; i < parts.length; i++) {
            intArray[i] = Integer.parseInt(parts[i]);
        }
        return intArray;
    }

    public static String clockMatrixArrayToString(int[] a){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length; i++) {
            // Append the integer to the StringBuilder
            result.append(a[i]);
            
            // Append a colon (':') if it's not the last element
            if (i < a.length - 1) {
                result.append(":");
            }
        }

        // Convert the StringBuilder to a String
        return result.toString();
    }

    
}