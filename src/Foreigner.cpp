#include "Foreigner.h"
#include <iostream>
#include <chrono>
#include <thread>

void Foreigner::discoverPlatoon() {
    // Implementation for discovering the platoon
    std::cout << "Foreigner discovering platoon" << std::endl;
}

Foreigner::Foreigner(int id, std::string &direction, float speed, GPSLocation &destination, GPSLocation &location)
        : Truck(id, direction, speed, destination, location) {}

void Foreigner::operator()() {
    int waitingTime = 5;
    int timeSinceLastWait = 0;
    while (true){
        if (state == "discovery"){
            if (waitingTime == timeSinceLastWait){
                printf("No suitable platoon found. Pulling over");
                this->stop();
            }
            printf("searching for platoon...");
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(1000));
    }
}
