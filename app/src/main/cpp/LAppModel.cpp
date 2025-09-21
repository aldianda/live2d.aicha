#include "LAppModel.hpp"
#include "rapidjson/include/rapidjson/document.h"
#include <vector>
#include <string>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <android/log.h>

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

using namespace Csm;
using namespace Csm::Rendering;


bool ReadAssetToBuffer(AAssetManager *mgr, const std::string &assetPath, std::vector<uint8_t> &outBuffer) {
    AAsset *asset = AAssetManager_open(mgr, assetPath.c_str(), AASSET_MODE_STREAMING);
    if (!asset) return false;
    off_t fileSize = AAsset_getLength(asset);
    outBuffer.resize(fileSize);
    int readBytes = AAsset_read(asset, outBuffer.data(), fileSize);
    AAsset_close(asset);
    return readBytes == fileSize;
}


bool LoadAssetToBuffer(AAssetManager* mgr, const std::string& path, std::vector<uint8_t>& outBuf) {
    AAsset* asset = AAssetManager_open(mgr, path.c_str(), AASSET_MODE_BUFFER);
    if (!asset) return false;
    size_t size = AAsset_getLength(asset);
    outBuf.resize(size);
    int read = AAsset_read(asset, outBuf.data(), size);
    AAsset_close(asset);
    return read == size;
}

GLuint LoadTextureFromAsset(AAssetManager* mgr, const std::string& path) {
    std::vector<uint8_t> buf;
    if (!LoadAssetToBuffer(mgr, path, buf)) return 0;

    int w, h, comp;
    unsigned char* img = stbi_load_from_memory(buf.data(), buf.size(), &w, &h, &comp, STBI_rgb_alpha);
    if (!img) return 0;

    GLuint texId;
    glGenTextures(1, &texId);
    glBindTexture(GL_TEXTURE_2D, texId);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, img);
    glBindTexture(GL_TEXTURE_2D, 0);

    stbi_image_free(img);
    return texId;
}

LAppModel::LAppModel() : _userTimeSeconds(0.0f), _renderer(nullptr) {}

LAppModel::~LAppModel() {
    if (_renderer) {
        CubismRenderer_OpenGLES2::Delete(_renderer);
        _renderer = nullptr;
    }
}

void LAppModel::CreateRenderer()
{
    if (_renderer) return; // already created

    _renderer = static_cast<CubismRenderer_OpenGLES2*>(CubismRenderer_OpenGLES2::Create());

    if (_renderer == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, "LAppModel", "Renderer creation FAILED!");
    } else {
        __android_log_print(ANDROID_LOG_INFO, "LAppModel", "Renderer created OK!");
    }
    if (_renderer) {
        _renderer->Initialize(this->GetModel());
        _renderer->SetClippingMaskBufferSize(256, 256);
    } else {
        LAppPal::PrintLog("[LAppModel] Renderer creation failed!");
    }
}


void LAppModel::LoadAssets(AAssetManager* mgr, const std::string& dir, const std::string& filename)
{
    _modelDir = dir + "/";
    std::string jsonPath = _modelDir + filename;
    LAppPal::PrintLog("[LAppModel] LoadAssets: %s", jsonPath.c_str());

    // --- 1. Load model3.json into buffer ---
    std::vector<uint8_t> jsonBuf;
    if (!LoadAssetToBuffer(mgr, jsonPath, jsonBuf)) {
        LAppPal::PrintLog("[LAppModel] ERROR: gagal buka JSON %s", jsonPath.c_str());
        return;
    }

    // --- 2. Parse JSON as CubismModelSettingJson ---
    _modelSetting = new CubismModelSettingJson(jsonBuf.data(), jsonBuf.size());

    // --- 3. Get moc3 file path ---
    std::string moc3Path = _modelDir + _modelSetting->GetModelFileName();

    // --- 4. Load moc3 file ---
    std::vector<uint8_t> mocBuf;
    if (!LoadAssetToBuffer(mgr, moc3Path, mocBuf)) {
        LAppPal::PrintLog("[LAppModel] ERROR: gagal load moc3 %s", moc3Path.c_str());
        return;
    }

    // --- 5. Create Cubism model ---
    this->LoadModel(mocBuf.data(), static_cast<csmSizeType>(mocBuf.size()));
    if (!this->GetModel()) {
        LAppPal::PrintLog("[LAppModel] ERROR: Model gagal di-load dari moc3!");
        return;
    }

    // --- 6. Create renderer (OpenGLES2) ---
    if (this->GetModel()) {
        CreateRenderer();
    }
    if (!_renderer) {
        LAppPal::PrintLog("[LAppModel] ERROR: Renderer gagal dibuat!");
        return;
    }

    // --- 7. Load and bind textures ---
    const int texCount = _modelSetting->GetTextureCount();
    for (int i = 0; i < texCount; ++i) {
        const char* texName = _modelSetting->GetTextureFileName(i);
        if (!texName) continue;

        std::string texPath = _modelDir + texName;
        GLuint texId = LoadTextureFromAsset(mgr, texPath);
        if (texId) {
            _renderer->BindTexture(i, texId);
            LAppPal::PrintLog("[LAppModel] Texture loaded: %s", texPath.c_str());
        } else {
            LAppPal::PrintLog("[LAppModel] ERROR: gagal load texture %s", texPath.c_str());
        }
    }
}


void LAppModel::Update(float deltaTimeSeconds) {
    _userTimeSeconds += deltaTimeSeconds;
}

void LAppModel::Draw() {
    if (_renderer) {
        LAppPal::PrintLog("[LAppModel] Try Draw Frame...");
        _renderer->PreDraw();
        _renderer->DrawModel();
        _renderer->PostDraw();
        LAppPal::PrintLog("[LAppModel] Draw Model Done.");
    } else {
        LAppPal::PrintLog("[LAppModel] Renderer NULL!");
    }
}


extern AAssetManager* g_assetManager;

void LAppModel::LoadAssets(const std::string& dir, const std::string& filename) {
    if (g_assetManager) {
        LoadAssets(g_assetManager, dir, filename);
    } else {
        LAppPal::PrintLog("[LAppModel] AssetManager belum di-set, load gagal");
    }
}

