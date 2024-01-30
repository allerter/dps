package dps.truck;

public class SocketAddress {
    String ipAddress;
    int port;

    public SocketAddress(String ipAddress, int port){
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return this.ipAddress + ":" + this.port;
    }

    public static SocketAddress fromString(String s) {
        String[] matches = s.split(":");
        return new SocketAddress(matches[0], Integer.valueOf(matches[1]));
    }
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same object reference, they are equal
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false; // Different classes or null, not equal
        }

        SocketAddress other = (SocketAddress) obj; // Typecast to your class
        return this.ipAddress == other.ipAddress && this.port == other.port; // Compare the relevant fields for equality
    }
}
