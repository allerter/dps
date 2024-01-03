#include "ForeignerTruck.h"

ForeignerTruck::ForeignerTruck(int id, GPSLocation initialLocation)
    : Truck(id, initialLocation) {
    setSpeed(20.0); //   ForeignerTruck's initial speed
}

void ForeignerTruck::discoverPlatoon() {
    std::lock_guard<std::mutex> lock(mtx);
    std::cout << "Foreigner truck " << truckID << " discovered a platoon." << std::endl;
}
