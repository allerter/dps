#include "PlatoonTruck.h"
#include "ForeignerTruck.h"

int main() {
    GPSLocation initialLocation(40.0, -74.0);
    PlatoonTruck platoonTruck(1, initialLocation);
    

    ForeignerTruck foreignerTruck(2, initialLocation);

    std::thread thread1(&PlatoonTruck::simulateMovement, &platoonTruck);
    std::thread thread2(&ForeignerTruck::simulateMovement, &foreignerTruck);

    thread1.join();
    thread2.join();

    return 0;
}
