#include "CollisionSensor.h"
#include <cmath>  

CollisionSensor::CollisionSensor() : status("OK") {}

void CollisionSensor::checkCollision(const GPSLocation& location1, const GPSLocation& location2) {
    // Simulated collision check based on distance
    // Here, we are checking if the distance between two trucks is less than a threshold
    double distanceThreshold = 2.0; // Set your desired distance threshold here

    // Calculate the distance between locations (Euclidean distance formula)
    double distance = std::sqrt(std::pow(location2.latitude - location1.latitude, 2) +
        std::pow(location2.longitude - location1.longitude, 2));

    // Check if the distance is less than the threshold
    if (distance < distanceThreshold) {
        status = "Collision Detected";
    }
    else {
        status = "OK";
    }
}

std::string CollisionSensor::getStatus() const {
    return status;
}
