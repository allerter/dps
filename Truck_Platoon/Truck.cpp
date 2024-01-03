#include "Truck.h"

Truck::Truck(int id, GPSLocation initialLocation)
    : truckID(id), location(initialLocation), destination(0.0, 0.0), currentSpeed(0.0), collisionSensor() {}

void Truck::setSpeed(double speed) {
    std::lock_guard<std::mutex> lock(mtx);
    currentSpeed = speed;
}

void Truck::accelerate() {
    std::lock_guard<std::mutex> lock(mtx);
    currentSpeed += 5.0;
    if (currentSpeed < 0.0) {
        currentSpeed = 0.0;
    }
}

void Truck::brake() {
    std::lock_guard<std::mutex> lock(mtx);
    currentSpeed -= 5.0;
    if (currentSpeed < 0.0) {
        currentSpeed = 0.0;
    }
}

void Truck::changeDirection(std::string newDirection) {
    std::lock_guard<std::mutex> lock(mtx);
    direction = newDirection;
}

void Truck::changeDestination(GPSLocation newDestination) {
    std::lock_guard<std::mutex> lock(mtx);
    destination = newDestination;
}

void Truck::simulateMovement() {
    // Assuming truck2Location represents the other truck's location in the simulation
    GPSLocation truck2Location(40.5, -73.9); // Replace with the actual GPS coordinates

    while (true) {
        std::this_thread::sleep_for(std::chrono::seconds(1));

        std::lock_guard<std::mutex> lock(mtx);

        // Assuming this truck's location is stored in 'this->location'
        collisionSensor.checkCollision(this->location, truck2Location);

        location.latitude += 0.1;
        location.longitude += 0.1;

        std::cout << "Truck " << truckID << " - Speed: " << std::abs(currentSpeed)
            << " Direction: " << direction
            << " Location: (" << location.latitude << ", " << location.longitude << ")"
            << " Collision Status: " << collisionSensor.getStatus() << "\n";
    }
}