#ifndef GPS_LOCATION_H
#define GPS_LOCATION_H

class GPSLocation {
public:
    double latitude;
    double longitude;

    GPSLocation(double lat, double lon) : latitude(lat), longitude(lon) {}
};

#endif  
