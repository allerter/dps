#include <iostream>
#include "Leader.h"
#include <thread>

int main() {
    // initialize platoon:
    // 1 Leader, 1 Prime Follower, 2 Followers

    printf("beginning");
    std::vector<Foreigner> trucks = {
            Foreigner(1, "straight", 0,  *new GPSLocation(50.110924, 8.682127), *new GPSLocation(50.5136, 7.4653)),
            Foreigner(2, "straight", 0,  *new GPSLocation(50.110924, 8.682127), *new GPSLocation(49.5136, 7.4653)),
            Foreigner(3, "straight", 0,  *new GPSLocation(50.110924, 8.682127), *new GPSLocation(48.5136, 7.4653)),
    };
    printf("created foreginerns");
    std::thread leader = std::thread(
            Leader(
                    0,
                    "straight",
                    0,
                   *new GPSLocation(50.110924, 8.682127),
                   *new GPSLocation(51.5136, 7.4653),
                   trucks));
    printf("created ledaer");
    //system("pause");
    // std::thread th1(trucks[0]);
    // std::thread th2(trucks[1]);
    // std::thread th3(trucks[2]);

    // start discovery by leader


    // start platooning by leader
    //th3.join();
    printf("\nbefore leader join\n");
    leader.join();
    printf("leader joined");
    return 0;
}
