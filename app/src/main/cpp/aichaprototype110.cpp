#include <jni.h>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <string>

#include "CubismFramework.hpp"
#include "LAppAllocator.hpp"
#include "LAppModel.hpp"

using namespace Live2D::Cubism::Framework;

#define LOG_TAG "Live2D"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static LAppAllocator g_allocator;
static AAssetManager* g_assetManager = nullptr;
static LAppModel* g_model = nullptr;

extern "C" {

// ✅ Set AssetManager dari Java
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_setAssetManager(
        JNIEnv* env,
        jclass /*clazz*/,
        jobject assetManager) {
    g_assetManager = AAssetManager_fromJava(env, assetManager);
    if (g_assetManager) {
        LOGI("AAssetManager set successfully");
    } else {
        LOGE("Failed to set AAssetManager");
    }
}

// ✅ Init Live2D
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_initLive2D(
        JNIEnv* env,
        jclass clazz) {
    LOGI("Initializing Live2D...");

    CubismFramework::Option option;
    option.LoggingLevel = CubismFramework::Option::LogLevel_Verbose;

    if (!CubismFramework::StartUp(&g_allocator, &option)) {
        LOGE("CubismFramework::StartUp failed!");
        return;
    }

    CubismFramework::Initialize(&option);
    LOGI("CubismFramework initialized");

    // load model di sini nanti (cek asset manager dulu)
    if (!g_assetManager) {
        LOGE("AssetManager is NULL, model cannot be loaded!");
        return;
    }

    // sementara log dulu
    LOGI("Ready to load model JSON & moc3 using AAssetManager");
}

// ✅ OnSurfaceCreated
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onSurfaceCreated(
        JNIEnv* env,
        jclass clazz) {
    LOGI("onSurfaceCreated called");

    if (!g_model) {
        g_model = new LAppModel();
        LOGI("LAppModel created");

        // load asset Live2D pertama kali
        g_model-> LoadAssets(g_assetManager, "app/src/main/assets/Icha", "Icha.model3.json");
    }
}

// ✅ OnSurfaceChanged
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onSurfaceChanged(
        JNIEnv* env,
        jclass clazz,
        jint width,
        jint height) {
    LOGI("onSurfaceChanged: width=%d, height=%d", width, height);
}

// ✅ OnDrawFrame
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onDrawFrame(
        JNIEnv* env,
        jclass clazz) {
    LOGI("onDrawFrame start");

    if (!g_model) {
        LOGE("g_model is NULL! Renderer cannot draw");
        return;
    }

    g_model->Update(1);
    g_model->Draw();
    LOGI("onDrawFrame finished");
}

// ✅ Destroy
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_destroy(
        JNIEnv* env,
        jclass clazz) {
    LOGI("Destroy called");

    delete g_model;
    g_model = nullptr;

    CubismFramework::Dispose();
    LOGI("CubismFramework disposed");
}

} // extern "C"
