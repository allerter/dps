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
    
}
