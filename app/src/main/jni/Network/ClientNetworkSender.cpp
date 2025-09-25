#include "ClientNetworkSender.h"
#include "BGFX/Logger.h"

#include <unistd.h>
#include <cstring>
#include <sys/socket.h>
#include <cerrno>

void ClientNetworkSender::send(int sockfd, const std::string& data) {
    if (sockfd < 0) {
        BGFX_LOG_ERROR("Cannot send data: invalid socket");
        return;
    }

    std::string message = data + "\n";

    // Force global scope send
    ssize_t sentBytes = ::send(sockfd, message.c_str(), message.size(), 0);
    if (sentBytes < 0) {
        BGFX_LOG_ERROR("Failed to send data: %s", strerror(errno));
    } else {
        BGFX_LOG_DEBUG("Sent %zd bytes to server", sentBytes);
    }
}
