#include "Message.h"
#include <iostream>

Message::Message(int id, const std::string& type, const std::unordered_map<std::string, std::string>& body)
    : id(id), type(type), body(body) {}

int Message::getId() const {
    return id;
}

std::string Message::getType() const {
    return type;
}

std::unordered_map<std::string, std::string> Message::getBody() const {
    return body;
}
