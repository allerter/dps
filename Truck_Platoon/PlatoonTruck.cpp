#include "PlatoonTruck.h"

PlatoonTruck::PlatoonTruck(int id, GPSLocation initialLocation)
    : Truck(id, initialLocation) {
    setSpeed(30.0); // PlatoonTruck's initial speed
}

void PlatoonTruck::sendMessageToLeader(const TruckMessage& m) {
    std::lock_guard<std::mutex> lock(mtx);
    std::cout << "Truck " << truckID << " sends a message to the leader: " << m.content << std::endl;
}

void PlatoonTruck::processReceivedMessage(const TruckMessage& m) {
    std::lock_guard<std::mutex> lock(mtx);
    std::cout << "Truck " << truckID << " processes the received message: " << m.content << std::endl;
}

void PlatoonTruck::disconnect() {
    std::lock_guard<std::mutex> lock(mtx);
    std::cout << "Truck " << truckID << " disconnects from the platoon." << std::endl;
}
