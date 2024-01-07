package src;

public class Main {
    public static void main(String[] args) {
        PlatoonTruck truck1 = new PlatoonTruck("127.0.0.1", 5000);
        PlatoonTruck truck2 = new PlatoonTruck("127.0.0.1", 5001);

        // Give some time for the server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Send a test message
        truck1.sendMessageTo("Hello Truck 2", "127.0.0.1", 5001);
    }
}
