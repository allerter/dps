#ifndef PLATOON_H
#define PLATOON_H

class Leader {};
class PrimeFollower {};
class Follower {};

class Platoon {
public:
    Leader leader;
    PrimeFollower primeFollower;
    Follower follower1;
    Follower follower2;

    Platoon() : leader(), primeFollower(), follower1(), follower2() {}
};

#endif // PLATOON_H
