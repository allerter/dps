//
// Created by Hazhir on 12/24/2023.
//

#ifndef DPS_PLATOONTRUCK_H
#define DPS_PLATOONTRUCK_H


#include "Truck.h"

class PlatoonTruck: private Truck {
public:
    void sendMessageToLeader(Message m);
    void processReceivedMessage(Message m);
    void disconnect();
};


#endif //DPS_PLATOONTRUCK_H
