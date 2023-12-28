cmake_minimum_required(VERSION 3.27)
project(dps)

set(CMAKE_CXX_STANDARD 23)

add_executable(dps src/main.cpp
        src/Truck.cpp
        src/Truck.h
        src/GPSLocation.cpp
        src/GPSLocation.h
        src/PlatoonTruck.cpp
        src/PlatoonTruck.h
        src/CollisionSensor.cpp
        src/CollisionSensor.h
        src/Platoon.cpp
        src/Platoon.h
        src/Leader.h
        src/Foreigner.cpp
        src/Follower.cpp
        src/Leader.cpp
        src/Message.cpp
        src/PrimeFollower.cpp)
