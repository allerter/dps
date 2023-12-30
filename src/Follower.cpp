
#include <string>
#include "GPSLocation.h"
#include "PlatoonTruck.h"
#include "Follower.h"

Follower::Follower(int id, const std::string &direction, float speed, GPSLocation &destination, GPSLocation &location)
        : PlatoonTruck(id, direction, speed, destination, location) {}

void Follower::operator()() {
}
