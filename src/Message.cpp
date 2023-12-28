#include "Message.h"
#include <iostream>
#include <utility>

Message::Message(int id, std::string  type, const std::unordered_map<std::string, std::string>& body)
    : id(id), type(std::move(type)), body(body) {}

int Message::getId() const {
    return id;
}

std::string Message::getType() const {
    return type;
}

std::unordered_map<std::string, std::string> Message::getBody() const {
    return body;
}
