#include <jni.h>
#include <android/log.h>
#include <GLES2/gl2.h>
#include "LAppLive2DManager.hpp"

#define LOG_TAG "Live2D"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

static LAppLive2DManager* live2DManager = nullptr;

extern "C" {

// PERUBAHAN: Sesuaikan nama fungsi dengan package dan kelas Java
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_initLive2D(
        JNIEnv* env,
jclass clazz
) {
LOGD("Live2D Initialized");
if (!live2DManager) {
live2DManager = new LAppLive2DManager();
live2DManager->Initialize();
}
}

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onSurfaceCreated(
        JNIEnv* env,
jclass clazz
) {
LOGD("Surface Created");
glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
}

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onSurfaceChanged(
        JNIEnv* env,
jclass clazz,
        jint width,
jint height
) {
LOGD("Surface Changed: %dx%d", width, height);
glViewport(0, 0, width, height);
if (live2DManager) {
live2DManager->Resize(width, height);
}
}

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onDrawFrame(
        JNIEnv* env,
jclass clazz
) {
glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

if (live2DManager) {
live2DManager->Update();
live2DManager->Render();
}
}

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_destroy(
        JNIEnv* env,
jclass clazz
) {
LOGD("Destroying Live2D");
if (live2DManager) {
delete live2DManager;
live2DManager = nullptr;
}
}

// Tambahkan fungsi yang belum ada
JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_setAssetManager(
        JNIEnv* env,
jclass clazz,
        jobject assetManager
) {
// Implementasi pengaturan AssetManager
AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
if (live2DManager && mgr) {
live2DManager->SetAssetManager(mgr);
}
}

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_live2d_demo_JniBridgeJava_onTouch(
        JNIEnv* env,
jclass clazz,
        jfloat x,
jfloat y
) {
if (live2DManager) {
live2DManager->OnTouch(x, y);
}
}

} // extern "C"