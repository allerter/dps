//
// Created by Hazhir on 12/24/2023.
//

#include "PlatoonTruck.h"

#include <utility>

PlatoonTruck::PlatoonTruck(int id, std::string direction, float speed, GPSLocation &destination, GPSLocation &location)
        : Truck(id, std::move(direction), speed, destination, location) {}

void PlatoonTruck::operator()() {
}
