#include <jni.h>
#include <GLES2/gl2.h>
#include <android/log.h>

#include "LAppLive2DManager.hpp"
#include "../../../CubismSdkForNative-5-r.3/Framework/src/CubismFramework.hpp"
#include "LAppAllocator.hpp"

#define LOG_TAG "Live2DRenderer"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

using namespace Csm;

static LAppAllocator s_allocator;
static LAppLive2DManager* s_live2DManager = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_Live2DView_initNative(JNIEnv* env, jobject thiz, jint width, jint height)
{
    LOGI("initNative() called");

    CubismFramework::Option option;
    option.LogFunction = NULL;
    option.LoggingLevel = CubismFramework::Option::LogLevel_Verbose;

    if (!CubismFramework::IsStarted()) {
        CubismFramework::StartUp(&s_allocator, &option);
        LOGI("CubismFramework::StartUp() OK");
    }

    if (!CubismFramework::IsInitialized()) {
        CubismFramework::Initialize(&option);
        LOGI("CubismFramework::Initialize() OK");
    }

    if (!s_live2DManager) {
        s_live2DManager = new LAppLive2DManager();
        s_live2DManager->Initialize(width, height);  // ✅ width & height dari Java
        LOGI("LAppLive2DManager created & initialized");
    }
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_Live2DView_renderNative(JNIEnv* env, jobject thiz) {
    // Set the clear color (R, G, B, A) - e.g., magenta
    glClearColor(1.0f, 0.0f, 1.0f, 1.0f); // Magenta (change to your desired color)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    if (s_live2DManager) {
        s_live2DManager->OnUpdate();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_Live2DView_releaseNative(JNIEnv* env, jobject thiz) {
    LOGI("releaseNative() called");

    if (s_live2DManager) {
        delete s_live2DManager;
        s_live2DManager = nullptr;
    }

    CubismFramework::Dispose();
    CubismFramework::CleanUp();

    LOGI("CubismFramework disposed & cleaned up");
}

