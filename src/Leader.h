#ifndef LEADER_H
#define LEADER_H

#include <vector>
#include "Message.h"
#include "Foreigner.h"
// #include "Platoon.h"
#include "PlatoonTruck.h"
#include "Truck.h"

class Leader : private PlatoonTruck {
private:
    // Platoon platoon;
    std::vector<Foreigner> trucks;
public:
    Leader(int id, std::string direction, float speed, GPSLocation &destination, GPSLocation &location, std::vector<Foreigner>& trucks);
    ~Leader();
// Leader(Platoon* platoon);
    void broadcast(const Message& message);
   // void removeFollower(PlatoonTruck* truck);
    void addFollower(Foreigner* truck);
    void startPlatoon();
    void discover();

    // [[nodiscard]] const Platoon &getPlatoon() const;

    // void setPlatoon(const Platoon &platoon);

    void removeFollower(PlatoonTruck *truck);

    void operator()();

};

#endif 
