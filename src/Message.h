#ifndef MESSAGE_H
#define MESSAGE_H

#include <string>
#include <unordered_map>

class Message {
private:
    int id;
    std::string type;
    std::unordered_map<std::string, std::string> body;

public:
    Message(int id, std::string  type, const std::unordered_map<std::string, std::string>& body);
    int getId() const;
    std::string getType() const;
    std::unordered_map<std::string, std::string> getBody() const;
};

#endif // MESSAGE_H
