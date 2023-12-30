#ifndef PRIMEFOLLOWER_H
#define PRIMEFOLLOWER_H


#include <bits/unique_ptr.h>
#include "PlatoonTruck.h"
// #include "Platoon.h"

class PrimeFollower : public PlatoonTruck {
private:
    // std::unique_ptr<Platoon> platoon;

public:
    PrimeFollower(int id, const std::string &direction, float speed, GPSLocation &destination, GPSLocation &location);

    void operator()();
    void replaceLeader();
};

#endif // PRIMEFOLLOWER_H
