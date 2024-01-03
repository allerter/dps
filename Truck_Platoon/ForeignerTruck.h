#ifndef FOREIGNER_TRUCK_H
#define FOREIGNER_TRUCK_H

#include "Truck.h"

class ForeignerTruck : public Truck {
public:
    ForeignerTruck(int id, GPSLocation initialLocation);

    void discoverPlatoon();
};

#endif  
