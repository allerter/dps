#include "Leader.h"
#include <iostream>

Leader::Leader(Platoon* platoon) : platoon(platoon) {}

void Leader::broadcast(const Message& message) {
    // Implementation for broadcasting a message
    std::cout << "Leader broadcasting: " << message.getBody() << std::endl;
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
