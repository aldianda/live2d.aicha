#include <jni.h>
#include <string>
#include <android/log.h>
#include "CubismFramework.hpp"
#include "LAppAllocator.hpp"

#define LOG_TAG "Live2D_JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

using namespace Live2D::Cubism::Framework;

// ✅ Allocator global
LAppAllocator allocator;

extern "C" {

// ✅ JNI function 1
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_renderer_Live2DNativeBridge_initializeLive2D(
        JNIEnv* env,
jobject /* this */)
{
LOGI("Initializing Live2D...");

CubismFramework::Option cubismOption;
cubismOption.LoggingLevel = CubismFramework::Option::LogLevel_Verbose;

if (!CubismFramework::StartUp(&allocator, &cubismOption)) {
LOGI("Failed to initialize Live2D Cubism!");
return;
}

CubismFramework::Initialize(&cubismOption);
LOGI("Live2D initialized successfully");
}

// ✅ JNI function 2
JNIEXPORT jstring JNICALL
Java_com_example_aichaprototype110_renderer_Live2DNativeBridge_stringFromJNI(
        JNIEnv* env,
        jobject /* this */)
{
    std::string hello = "Hello from Live2D JNI!";
    LOGI("%s", hello.c_str());
    return env->NewStringUTF(hello.c_str());
}

} // ✅ extern "C" ends here
