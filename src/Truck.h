//
// Created by Hazhir on 12/22/2023.
//

#ifndef DPS_TRUCK_H
#define DPS_TRUCK_H


#include <string>
#include "GPSLocation.h"
#include "CollisionSensor.h"

class Truck
{
private:
    const int id;
    int messageCounter{0};
public:
    int getMessageCounter() const;

    void setMessageCounter(int messageCounter);

private:
    std::string direction;
    float speed;
    GPSLocation destination;
    GPSLocation location;
    CollisionSensor collisionSensor;

protected:
    std::string state;
public:

    Truck(int id, std::string  direction, float speed, GPSLocation& destination,
          GPSLocation& location);

    ~Truck();

    virtual void operator()();
    void stop();
    [[nodiscard]] int getId() const;

    void setDirection(const std::string &direction);

    void setSpeed(float speed);

    void setDestination(const GPSLocation &destination);

    [[nodiscard]] GPSLocation getLocation();

    void setLocation(const GPSLocation &location);

    [[nodiscard]] std::string getDirection() const;

    [[nodiscard]] float getSpeed() const;

    [[nodiscard]] GPSLocation getDestination() const;

    void changeSpeed(float newSpeed);
    void changeDirection(std::string &newDirection);
    void changeDestination(GPSLocation &newDestination);

    int incrementAndGetMessageCounter();
};

#endif //DPS_TRUCK_H
