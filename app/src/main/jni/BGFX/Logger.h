#pragma once

namespace BGFX {

class Logger {
public:
    enum Level {
        INFO,
        DEBUG,
        ERROR,
        FATAL
    };

    static void Log(Level level, const char* format, ...);
    static void Info(const char* format, ...);
    static void Debug(const char* format, ...);
    static void Error(const char* format, ...);
    static void Fatal(const char* format, ...);
};
}

#define BGFX_LOG_INFO(str, ...)  BGFX::Logger::Info(str, ##__VA_ARGS__)
#define BGFX_LOG_DEBUG(str, ...) BGFX::Logger::Debug(str, ##__VA_ARGS__)
#define BGFX_LOG_ERROR(str, ...) BGFX::Logger::Error(str, ##__VA_ARGS__)
#define BGFX_LOG_FATAL(str, ...) BGFX::Logger::Fatal(str, ##__VA_ARGS__)

