//
// Created by Hazhir on 12/24/2023.
//

#ifndef DPS_PLATOONTRUCK_H
#define DPS_PLATOONTRUCK_H


#include "Truck.h"
#include "Message.h"

class PlatoonTruck: public Truck {
private:
    std::string state;
public:

    PlatoonTruck(int id, std::string &direction, float speed, GPSLocation &destination, GPSLocation &location);

    void sendMessageToLeader(Message m);
    void processReceivedMessage(Message m);
    void disconnect();
};


#endif //DPS_PLATOONTRUCK_H
