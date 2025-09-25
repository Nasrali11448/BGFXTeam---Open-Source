#include "Logger.h"
#include "Includes/obfuscate.h"
#include <cstdio>
#include <ctime>
#include <cstdarg>

namespace BGFX {

namespace {
    const char* LevelToStr(Logger::Level level) {
        switch (level) {
            case Logger::INFO:  return OBFUSCATE("INFO");
            case Logger::DEBUG: return OBFUSCATE("DEBUG");
            case Logger::ERROR: return OBFUSCATE("ERROR");
            case Logger::FATAL: return OBFUSCATE("FATAL");
            default:            return OBFUSCATE("UNKNOWN");
        }
    }

    const char* GetLogPath() {
        return OBFUSCATE("/sdcard/BGFX/bgfx_log.txt");
    }

    void InitOnce() {
    static bool done = false;
    if (done) return;

    FILE* file = fopen(GetLogPath(), "w");
    if (file) {
        time_t now = time(nullptr);
        struct tm* t = localtime(&now);
        fprintf(file,
            "----------------------------------------------------------------------------------------------------\n"
            "LOG DATE: %02d-%02d-%04d %02d:%02d:%02d\n"
            "----------------------------------------------------------------------------------------------------\n",
            t->tm_mday, t->tm_mon + 1, t->tm_year + 1900,
            t->tm_hour, t->tm_min, t->tm_sec
        );
        fclose(file);
    }

    done = true;
}

    void LogWithVaList(Logger::Level level, const char* format, va_list args) {
        InitOnce();

        FILE* file = fopen(GetLogPath(), "a");
        if (!file) return;

        time_t now = time(nullptr);
        struct tm* t = localtime(&now);

        fprintf(file, "(BGFX) %04d-%02d-%02d %02d:%02d:%02d [%s]: ",
            t->tm_year + 1900, t->tm_mon + 1, t->tm_mday,
            t->tm_hour, t->tm_min, t->tm_sec,
            LevelToStr(level));

        vfprintf(file, format, args);
        fprintf(file, OBFUSCATE("\n"));
        fclose(file);
    }
}

void Logger::Log(Level level, const char* format, ...) {
    va_list args;
    va_start(args, format);
    LogWithVaList(level, format, args);
    va_end(args);
}

void Logger::Info(const char* format, ...) {
    va_list args;
    va_start(args, format);
    LogWithVaList(INFO, format, args);
    va_end(args);
}

void Logger::Debug(const char* format, ...) {
    va_list args;
    va_start(args, format);
    LogWithVaList(DEBUG, format, args);
    va_end(args);
}

void Logger::Error(const char* format, ...) {
    va_list args;
    va_start(args, format);
    LogWithVaList(ERROR, format, args);
    va_end(args);
}

void Logger::Fatal(const char* format, ...) {
    va_list args;
    va_start(args, format);
    LogWithVaList(FATAL, format, args);
    va_end(args);
}

}
