#include <jni.h>
#include <string>
#include <android/log.h>
#include <GLES2/gl2.h>
#include "CubismFramework.hpp"
#include "LAppAllocator.hpp"
#include "LAppModel.hpp"

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

}
static LAppModel* g_model = nullptr;
extern "C" JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onSurfaceChanged(JNIEnv*, jclass, jint width, jint height) {
    glViewport(0, 0, width, height);
    LOGI("Viewport set: %d x %d", width, height);
    if (g_model) {
        g_model->SetProjection(width, height); // tambahkan method ini
    }
}// ✅ extern "C" ends here

extern "C" JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onDrawFrame(JNIEnv*, jclass) {
    glClearColor(1.0f, 0.0f, 1.0f, 1.0f); // Magenta
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    LOGI("onDrawFrame called");

    if (g_model) {
        LOGI("Drawing model...");
        g_model->Update(1.0f/60.0f);
        g_model->Draw();
    } else {
        LOGI("g_model is null!");
    }
}
