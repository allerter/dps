//
// Created by Hazhir on 12/29/2023.
//

#include <string>
#include <ctime>

std::string now()
{
    std::time_t now= std::time(0);
    std::tm* now_tm= std::gmtime(&now);
    char buf[42];
    std::strftime(buf, 42, "%Y%m%d %X", now_tm);
    return buf;
}