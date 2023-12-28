#ifndef FOREIGNER_H
#define FOREIGNER_H

#include "Truck.h"

class Foreigner : public Truck {
public:
    Foreigner(int id, std::string &direction, float speed, GPSLocation &destination, GPSLocation &location);

    void discoverPlatoon();
    void operator()();

};


#endif // FOREIGNER_H
