//
// Created by Hazhir on 12/22/2023.
//

#ifndef DPS_GPSLOCATION_H
#define DPS_GPSLOCATION_H


class GPSLocation {
public:
    GPSLocation(float latitude, float longitude);

    [[nodiscard]] float getLatitude() const;

    void setLatitude(float latitude);

    [[nodiscard]] float getLongitude() const;

    void setLongitude(float longitude);

private:
    float latitude;
    float longitude;
};


#endif //DPS_GPSLOCATION_H
