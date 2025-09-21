#include <jni.h>
#include <string>
#include <android/log.h>
#include "CubismFramework.hpp"
#include "LAppAllocator.hpp"
#include "LAppLive2DManager.hpp"
#include <android/asset_manager_jni.h>


AAssetManager* g_assetManager = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_MainActivity_nativeSetAssetManager(
        JNIEnv *env, jobject /* this */, jobject assetManager) {
    g_assetManager = AAssetManager_fromJava(env, assetManager);
}


#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Live2D", __VA_ARGS__)

using namespace Csm;

// Allocator Live2D
static LAppAllocator s_allocator;

// JNI Init: kita terima width & height dari Kotlin
extern "C"
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_renderer_Live2DGLView_initNative(
        JNIEnv *env, jobject /*thiz*/, jint width, jint height)
{
LOGI("initNative() dipanggil");

// Setup opsi Live2D
CubismFramework::Option option;
option.LogFunction    = [](const csmChar* msg) { LOGI("%s", msg); };
option.LoggingLevel   = CubismFramework::Option::LogLevel_Verbose;

// StartUp & Initialize hanya sekali
if (!CubismFramework::IsStarted()) {
CubismFramework::StartUp(&s_allocator, &option);
CubismFramework::Initialize(&option);
LOGI("CubismFramework initialized");
}

// Buat & init Live2DManager (load model, atur viewport, dsb)
auto mgr = LAppLive2DManager::GetInstance();
mgr->Initialize(static_cast<int>(width), static_cast<int>(height));
}

// JNI Render: dipanggil tiap frame
extern "C"
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_renderer_Live2DGLView_renderNative(
        JNIEnv *env, jobject /*thiz*/)
{
// Update & draw model
auto mgr = LAppLive2DManager::GetInstance();
if (mgr) mgr->OnUpdate();
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_renderer_Live2DGLView_releaseNative(
        JNIEnv *env, jobject /*thiz*/)
{
LOGI("releaseNative() dipanggil");
LAppLive2DManager::ReleaseInstance();
CubismFramework::Dispose();
CubismFramework::CleanUp();
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_aichaprototype110_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello from C++");
}