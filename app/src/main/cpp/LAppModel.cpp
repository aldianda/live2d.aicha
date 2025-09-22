#include "LAppModel.hpp"
#include "rapidjson/include/rapidjson/document.h"
#include <vector>
#include <string>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
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


void LAppModel::LoadAssets(AAssetManager* mgr, const std::string& dir, const std::string& filename) {
    //_modelDir = dir + "/";
    _modelDir = "Icha/";
    std::string jsonPath = _modelDir + "shidiq.model3.json";
    LAppPal::PrintLog("[LAppModel] lokasi file : %s", jsonPath.c_str());
    // --- 1. Load model3.json dari assets ---
    std::vector<uint8_t> jsonBuf;
    if (!LoadAssetToBuffer(mgr, jsonPath, jsonBuf)) {
        LAppPal::PrintLog("[LAppModel] Gagal buka JSON dari asset: %s", jsonPath.c_str());
        return;
    }

    // --- 2. Parse JSON, ambil path moc3 ---
    rapidjson::Document doc;
    doc.Parse(reinterpret_cast<const char*>(jsonBuf.data()), jsonBuf.size());
    if (doc.HasParseError()) {
        LAppPal::PrintLog("[LAppModel] ERROR: JSON parse error model3.json %s", jsonPath.c_str());
        return;
    }
    if (!doc.HasMember("FileReferences") || !doc["FileReferences"].HasMember("Moc")) {
        LAppPal::PrintLog("[LAppModel] ERROR: model3.json ga ada path 'FileReferences.Moc'!");
        return;
    }
    std::string moc3Path = _modelDir + doc["FileReferences"]["Moc"].GetString();

    // --- 3. Load file .moc3 ---
    std::vector<uint8_t> mocBuf;
    LAppPal::PrintLog("[LAppModel] Lokasi moc3: %s", moc3Path.c_str());

    if (!LoadAssetToBuffer(mgr, moc3Path, mocBuf)) {
        LAppPal::PrintLog("[LAppModel] Gagal load moc3: %s", moc3Path.c_str());
        return;
    }

    // --- 4. Load model pakai buffer moc3 ---
    this->LoadModel(mocBuf.data(), static_cast<csmSizeType>(mocBuf.size()));

    if (!this->GetModel()) {
        LAppPal::PrintLog("[LAppModel] ERROR: Model gagal di-load. Cek file moc3!");
        return;
    }

    Csm::CubismModelMatrix* matrix = this->GetModelMatrix();
    if (matrix) {
        // Full width (ganti 1.0f/1.5f/2.0f sesuai kebutuhan)
        matrix->SetCenterPosition(0.0f, 0.0f);
        matrix->Translate(0.0f, 0.0f); // Jika ingin geser
        matrix->Scale(2.0f, 2.0f); // Scaling manual jika perlu
    }

    // --- 5. Renderer + load texture ---
    _renderer = dynamic_cast<CubismRenderer_OpenGLES2*>(CubismRenderer_OpenGLES2::Create());
    if (_renderer) {
        _renderer->Initialize(this->GetModel());
        _renderer->SetClippingMaskBufferSize(256, 256);

        ICubismModelSetting* setting = this->GetModelSetting();
        if (!setting) return;

        int texCount = setting->GetTextureCount();
        for (int i = 0; i < texCount; ++i) {
            const char* texName = setting->GetTextureFileName(i);
            if (!texName) continue;
            std::string texPath = _modelDir + texName;
            GLuint texId = LoadTextureFromAsset(mgr, texPath);
            if (texId)
                _renderer->BindTexture(i, texId);
            else
                LAppPal::PrintLog("[LAppModel] Gagal load texture: %s", texPath.c_str());
        }
    } else {
        LAppPal::PrintLog("[LAppModel] Gagal create renderer");
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

