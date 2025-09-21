// aichaprototype110.cpp
#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <GLES2/gl2.h>

#include "CubismFramework.hpp"
#include "LAppAllocator.hpp"
#include "LAppLive2DManager.hpp" // path may vary in your repo
#include "LAppModel.hpp"

#define LOG_TAG "Live2D_JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace Live2D::Cubism::Framework;

// global allocator and asset manager
static LAppAllocator g_allocator;
static AAssetManager* g_assetManager = nullptr;

extern "C" {

// set the AssetManager from Java so native can read assets
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_setAssetManager(
        JNIEnv* env,
        jclass /*clazz*/,
        jobject assetManager) {
    g_assetManager = AAssetManager_fromJava(env, assetManager);
    if (g_assetManager) {
        LOGI("AssetManager set in native code");
    } else {
        LOGE("Failed to get AAssetManager");
    }
}

// optional init function if you want a distinct init
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_initLive2D(
        JNIEnv* env,
        jclass /*clazz*/) {

    LOGI("initLive2D called");

    if (!CubismFramework::IsStarted()) {
        CubismFramework::Option option;
        option.LoggingLevel = CubismFramework::Option::LogLevel_Verbose;
        if (!CubismFramework::StartUp(&g_allocator, &option)) {
            LOGE("Cubism StartUp failed");
            return;
        }
        CubismFramework::Initialize(&option);
        LOGI("Cubism framework initialized");
    }

    // If you prefer, create model here; we create in onSurfaceChanged below to ensure GL context exists
}

// called when surface is created (GL context ready)
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onSurfaceCreated(
        JNIEnv* env,
        jclass /*clazz*/) {
    LOGI("onSurfaceCreated()");
    // nothing heavy here; we just ensure Cubism is started
    if (!CubismFramework::IsStarted()) {
        Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_initLive2D(env, nullptr);
    }
}

// called when surface changed (width/height known) -> we can create model safely and set viewport
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onSurfaceChanged(
        JNIEnv* env,
        jclass /*clazz*/,
        jint width,
        jint height) {

    LOGI("onSurfaceChanged: %d x %d", width, height);
    // set viewport
    glViewport(0, 0, width, height);

    // create/load model if not loaded
    auto manager = LAppLive2DManager::GetInstance();
    if (manager->GetModel(0) == nullptr) {
        // IMPORTANT: adjust the model folder name to match your assets folder
        manager->CreateModel("Aicha");
        LOGI("Requested CreateModel(\"Aicha\")");
    } else {
        LOGI("Model already exists");
    }
}

// called each frame
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onDrawFrame(
        JNIEnv* env,
        jclass /*clazz*/) {

    auto manager = LAppLive2DManager::GetInstance();
    auto model = manager->GetModel(0);
    if (model) {
        model->Update();
        model->Draw();
    } else {
        LOGE("[LAppModel] Renderer NULL or model not created yet!");
    }
}

// destroy / release
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_destroy(
        JNIEnv* env,
        jclass /*clazz*/) {
    LOGI("destroy() called");
    auto manager = LAppLive2DManager::GetInstance();
    manager->ReleaseAllModels();

    if (CubismFramework::IsStarted()) {
        CubismFramework::Dispose();
        LOGI("Cubism framework disposed");
    }
}

} // extern "C"
