#ifndef LAPP_PAL_HPP
#define LAPP_PAL_HPP

#include <string>

class LAppPal {
public:
    static void PrintLog(const char* format, ...);
    static void PrintMessage(const std::string& message);
    static double GetDeltaTime();
};

#endif // LAPP_PAL_HPP
