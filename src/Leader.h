#ifndef LEADER_H
#define LEADER_H

 // #include "Platoon.h"
#include "Message.h"
//#include "PlatoonTruck.h"
#include "Foreigner.h"

class Leader {
private:
    //Platoon* platoon;

public:
   // Leader(Platoon* platoon);
    void broadcast(const Message& message);
   // void removeFollower(PlatoonTruck* truck);
    void addFollower(Foreigner* truck);
    void startPlatoon();
    void discover();
};

#endif 
