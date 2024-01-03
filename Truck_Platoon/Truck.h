#ifndef TRUCK_H
#define TRUCK_H

#include <iostream>
#include <thread>
#include <mutex>
#include "GPSLocation.h"
#include "CollisionSensor.h"
#include "TruckMessage.h"
#include <chrono>

class Truck {
protected:
    int truckID;
    double currentSpeed;
    std::string direction;
    GPSLocation destination;
    GPSLocation location;
    CollisionSensor collisionSensor; // Aggregation

    std::mutex mtx; // Mutex for thread safety

public:
    Truck(int id,const GPSLocation initialLocation);

    void setSpeed(double speed);

    void accelerate();

    void brake();

    void changeDirection(std::string newDirection);

    void changeDestination(GPSLocation newDestination);

    void simulateMovement();

   
};

#endif  
