#ifndef COLLISION_SENSOR_H
#define COLLISION_SENSOR_H

#include <string>
#include "GPSLocation.h" // Assuming GPSLocation is included

class CollisionSensor {
private:
    std::string status;

public:
    CollisionSensor();

    void checkCollision(const GPSLocation& location1, const GPSLocation& location2);

    std::string getStatus() const;
};

#endif // COLLISION_SENSOR_H
