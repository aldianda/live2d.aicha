#include <jni.h>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <GLES2/gl2.h>

#include <Live2D/Cubism/Core/csmModel.hpp>
#include <Live2D/Cubism/Core/csmMoc.hpp>

#define LOG_TAG "Live2D_Native"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace {
    csmModel* model = nullptr;
    csmMoc* moc = nullptr;
    AAssetManager* assetManager = nullptr;
}

extern "C" {

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_renderer_Live2DNativeLib_setAssetManager(
        JNIEnv* env,
        jobject thiz,
        jobject asset_mgr
) {
    assetManager = AAssetManager_fromJava(env, asset_mgr);
    LOGD("Asset manager initialized");
}

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_renderer_Live2DNativeLib_init(
        JNIEnv* env,
        jobject thiz,
        jstring model_path
) {
    if (!assetManager) {
        LOGE("Asset manager not set!");
        return;
    }

    const char* path = env->GetStringUTFChars(model_path, nullptr);
    LOGD("Loading Live2D model: %s", path);

    AAsset* asset = AAssetManager_open(assetManager, path, AASSET_MODE_BUFFER);
    env->ReleaseStringUTFChars(model_path, path);

    if (!asset) {
        LOGE("Failed to open model asset");
        return;
    }

    const void* buffer = AAsset_getBuffer(asset);
    off_t size = AAsset_getLength(asset);

    moc = csmReviveMocInPlace(const_cast<void*>(buffer), size);
    if (!moc) {
        LOGE("Failed to revive MOC");
        AAsset_close(asset);
        return;
    }

    model = csmInitializeModelInPlace(moc);
    if (!model) {
        LOGE("Failed to initialize model");
        csmDeleteMoc(moc);
        AAsset_close(asset);
        return;
    }

    AAsset_close(asset);
    LOGD("Live2D model loaded successfully");
}

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_renderer_Live2DNativeLib_update(
        JNIEnv* env,
        jobject thiz
) {
    if (!model) return;

    // Update model logic here
    // Example: csmSetParameterValue(model, "ParamAngleX", 0.5f);
    csmUpdateModel(model);
}

JNIEXPORT void JNICALL
Java_com_example_aichaprototype110_renderer_Live2DNativeLib_draw(
        JNIEnv* env,
        jobject thiz
) {
    if (!model) return;

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    csmDrawModel(model);
}

} // extern "C"