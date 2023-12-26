#ifndef PRIMEFOLLOWER_H
#define PRIMEFOLLOWER_H

#include "Platoon.h"

class PrimeFollower {
private:
    Platoon* platoon;

public:
    PrimeFollower(Platoon* platoon);
    void replaceLeader();
};

#endif // PRIMEFOLLOWER_H
