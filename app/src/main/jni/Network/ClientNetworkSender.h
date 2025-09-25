#ifndef CLIENT_NETWORK_SENDER_H
#define CLIENT_NETWORK_SENDER_H

#include <string>

class ClientNetworkSender {
public:
    static void send(int sockfd, const std::string& data);
};

#endif // CLIENT_NETWORK_SENDER_H