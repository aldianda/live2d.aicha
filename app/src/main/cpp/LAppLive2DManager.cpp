#include "LAppLive2DManager.hpp"
#include <CubismFramework.hpp>
#include <Rendering/OpenGL/CubismRenderer_OpenGLES2.hpp>
#include <__memory/unique_ptr.h>

using namespace Live2D::Cubism::Framework;
using namespace Live2D::Cubism::Framework::Rendering;

namespace {
    LAppLive2DManager* s_instance = nullptr;
}

LAppLive2DManager* LAppLive2DManager::GetInstance() {
    if (!s_instance) {
        s_instance = new LAppLive2DManager();
    }
    return s_instance;
}

void LAppLive2DManager::ReleaseInstance() {
    delete s_instance;
    s_instance = nullptr;
}

LAppLive2DManager::LAppLive2DManager()
        : _screenWidth(0), _screenHeight(0) {}

LAppLive2DManager::~LAppLive2DManager() {}

void LAppLive2DManager::Initialize(int width, int height) {
    _screenWidth = width;
    _screenHeight = height;

    CubismFramework::Initialize(nullptr);
    LoadModel();
}

void LAppLive2DManager::LoadModel() {
    _model = std::make_unique<LAppModel>();
    _model->LoadAssets("Icha", "Shidiq.model3.json");
}

void LAppLive2DManager::OnUpdate() {
    if (_model) {
        _model->Update(1.0f/60.0f);
        _model->Draw();
    }
}

void LAppLive2DManager::Resize(int width, int height) {
    _screenWidth = width;
    _screenHeight = height;
}
