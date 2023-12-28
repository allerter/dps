#include "Leader.h"
#include "Truck.h"
#include <iostream>
#include <utility>

void Leader::broadcast(const Message& message) {
    // Implementation for broadcasting a message
    // std::cout << "Leader broadcasting: " << message.getBody() << std::endl;
}

void Leader::removeFollower(PlatoonTruck* truck) {
    // Implementation for removing a follower
}

void Leader::addFollower(Foreigner* truck) {
    // Implementation for adding a foreigner as a follower
}

void Leader::startPlatoon() {
    // Implementation for starting the platoon
}

void Leader::discover() {
    // Implementation for discovering information
}

/*
const Platoon &Leader::getPlatoon() const {
    return platoon;
}
*/

/* void Leader::setPlatoon(const Platoon &platoon) {
    Leader::platoon = platoon;
}
 */

Leader::Leader(int id, std::string &direction, float speed, GPSLocation &destination, GPSLocation &location,
               std::vector<Truck>& trucks1)
        : PlatoonTruck(id, direction, speed, destination, location) {
    this->trucks = std::move(trucks1);
}

void Leader::operator()() {
    while (true) {
        for (auto & truck : trucks) {
            int truckId{truck.getId()};
            std::printf("%s", reinterpret_cast<const char *>(truckId));
        }
        printf("hi");
        break;
    }
}

Leader::~Leader() {

}


