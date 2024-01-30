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