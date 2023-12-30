#ifndef FOLLOWER_H
#define FOLLOWER_H

class Follower : PlatoonTruck {
public:
    Follower(int id, const std::string &direction, float speed, GPSLocation &destination, GPSLocation &location);
    void operator()();
};

#endif // FOLLOWER_H
