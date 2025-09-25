#ifndef CLIENT_NETWORK_H
#define CLIENT_NETWORK_H

#include <thread>
#include <atomic>
#include <string>

class ClientNetworkSender;

class ClientNetwork {
public:
    static ClientNetwork* Instance(); // Singleton instance

    void connectToServer(const std::string& ip, int port);
    void disconnect();
    bool isConnected() const;
    void sendScreenshotTest();
    void sendDeviceId();

    ClientNetworkSender* getSender();

private:
    ClientNetwork(); // Private constructor
    ~ClientNetwork();

    void listenLoop();

    int sockfd;
    std::thread listenerThread;
    std::atomic<bool> running;

    static ClientNetwork* instance;
    ClientNetworkSender* sender;
};

#endif // CLIENT_NETWORK_H
