#include "PrimeFollower.h"

void PrimeFollower::replaceLeader() {
    // Implementation for replacing the leader
}

PrimeFollower::PrimeFollower(int id, const std::string &direction, float speed, GPSLocation &destination,
                             GPSLocation &location) : PlatoonTruck(id, direction, speed, destination, location) {}

void PrimeFollower::operator()() {
}

