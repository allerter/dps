//
// Created by Hazhir on 12/24/2023.
//

#ifndef DPS_PLATOON_H
#define DPS_PLATOON_H

class Leader;
class PrimeFollower;

#include "Leader.h"
#include "PrimeFollower.h"
#include "Follower.h"

class Platoon {
    std::unique_ptr<Leader> leader;
    std::unique_ptr<PrimeFollower> primeFollower;
    std::vector<std::unique_ptr<Follower>> followers;
public:
    Platoon(const Leader &leader, const PrimeFollower &primeFollower, const std::vector<Follower> &followers);
};


#endif //DPS_PLATOON_H
