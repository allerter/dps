#ifndef PLATOON_TRUCK_H
#define PLATOON_TRUCK_H

#include "Truck.h"
#include "Platoon.h"
#include "TruckMessage.h"

class PlatoonTruck : public Truck {
private:
    Platoon platoon;

public:
    PlatoonTruck(int id, GPSLocation initialLocation);

    void sendMessageToLeader(const TruckMessage& m);

    void processReceivedMessage(const TruckMessage& m);

    void disconnect();
};

#endif // PLATOON_TRUCK_H
