#include "Leader.h"
#include "Truck.h"
#include "utils.h"
#include <iostream>
#include <utility>
#include <thread>

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

Leader::Leader(int id, std::string direction, float speed, GPSLocation &destination, GPSLocation &location, std::vector<Foreigner>& trucks1)
        : PlatoonTruck(id, std::move(direction), speed, destination, location) {
    this->trucks = std::move(trucks1);
}

void Leader::operator()() {
    printf("\nbeginning operator\n");
    int waitForJoin{0};
    state = "discovery";
    while (true) {
        processReceivedMessages();
        if (state == "discovery"){
            if (waitForJoin == 5 && joinedTrucks.length() != 3) {
                printf("Discovery unsuccessful. Trucked joined: " + std::to_string(joinedTrucks.length()));
            } else if (waitForJoin == 5 && joinedTrucks.length() == 3) {
                printf("Discovery successful. Starting journey...");
                state = "journey";
            }
            std::unordered_map<std::string, std::string> messageBody = {
                    {"utc", now()},
                    {"truckID", std::to_string(getId())},
                    {"address", "localhost:port"}
            };
            Message message(incrementAndGetMessageCounter(), "discovery", messageBody);
            broadcast(message);
            waitForJoin++;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(1000));
    }
}

Leader::~Leader() = default;

