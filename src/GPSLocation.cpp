//
// Created by Hazhir on 12/22/2023.
//

#include "GPSLocation.h"

float GPSLocation::getLatitude() const {
    return latitude;
}

void GPSLocation::setLatitude(float newLatitude) {
    GPSLocation::latitude = newLatitude;
}

float GPSLocation::getLongitude() const {
    return longitude;
}

void GPSLocation::setLongitude(float newLongitude) {
    GPSLocation::longitude = newLongitude;
}

GPSLocation::GPSLocation(float latitude, float longitude) : latitude(latitude), longitude(longitude) {}
