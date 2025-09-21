#include "LAppPal.hpp"
#include <android/log.h>
#include <ctime>

#define LOG_TAG "Live2D"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

namespace {
    std::clock_t s_lastTime;
}

void LAppPal::PrintLog(const char* format, ...)
{
    va_list args;
    va_start(args, format);
    __android_log_vprint(ANDROID_LOG_INFO, LOG_TAG, format, args);
    va_end(args);
}

void LAppPal::PrintMessage(const std::string& message)
{
    LOGI("%s", message.c_str());
}

double LAppPal::GetDeltaTime()
{
    std::clock_t now = std::clock();
    double deltaTime = static_cast<double>(now - s_lastTime) / CLOCKS_PER_SEC;
    s_lastTime = now;
    return deltaTime;
}
