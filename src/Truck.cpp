//
// Created by Hazhir on 12/22/2023.
//

#include "Truck.h"

#include <utility>

Truck::~Truck() = default;

int Truck::getId() const {
    return id;
}


void Truck::setDirection(const std::string &newDirection) {
    Truck::direction = newDirection;
}

void Truck::setSpeed(float newSpeed) {
    Truck::speed = newSpeed;
}

void Truck::setDestination(const GPSLocation &newDestination) {
    Truck::destination = newDestination;
}

GPSLocation Truck::getLocation() {
    return location;
}

void Truck::setLocation(const GPSLocation &newLocation) {
    Truck::location = newLocation;
}

Truck::Truck(int id, std::string direction, float speed, GPSLocation &destination,
             GPSLocation &location) : id(id), direction(std::move(direction)), speed(speed), destination(destination),
                                            location(location) {
    this->collisionSensor = *new CollisionSensor();
}

std::string Truck::getDirection() const {
    return direction;
}

float Truck::getSpeed() const {
    return speed;
}

GPSLocation Truck::getDestination() const {
    return destination;
}

void Truck::changeSpeed(float newSpeed) {
    this->speed = newSpeed;
}

void Truck::changeDestination(GPSLocation &newDestination) {
    this->destination = newDestination;
}

void Truck::changeDirection(std::string &newDirection) {
    Truck::direction = newDirection;
}

void Truck::stop() {
    printf("stopping");
    setDirection("right");
    setSpeed(10);
    setDirection("straight");
    setSpeed(0);
}

void Truck::operator()() {

}

int Truck::getMessageCounter() const {
    return messageCounter;
}

void Truck::setMessageCounter(int messageCounter1) {
    Truck::messageCounter = messageCounter1;
}

int Truck::incrementAndGetMessageCounter() {
    return ++Truck::messageCounter;
}