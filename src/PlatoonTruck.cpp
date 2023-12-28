//
// Created by Hazhir on 12/24/2023.
//

#include "PlatoonTruck.h"

PlatoonTruck::PlatoonTruck(int id, std::string &direction, float speed, GPSLocation &destination, GPSLocation &location)
        : Truck(id, direction, speed, destination, location) {}
