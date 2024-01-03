

#ifndef MESSAGE_H
#define MESSAGE_H

#include <string>

class TruckMessage {
public:
    std::string content;

    TruckMessage(const std::string& msg) : content(msg) {}
};

#endif // MESSAGE_H

