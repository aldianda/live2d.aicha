#ifndef LAPP_MODEL_HPP
#define LAPP_MODEL_HPP

#include <Type/CubismBasicType.hpp>
#include <Model/CubismUserModel.hpp>
#include <Rendering/OpenGL/CubismRenderer_OpenGLES2.hpp>
#include "CubismModelSettingJson.hpp"
#include "LAppPal.hpp"
#include <GLES2/gl2.h>
#include <android/asset_manager.h>
#include <string>

class LAppModel : public Csm::CubismUserModel {
public:
    LAppModel();
    virtual ~LAppModel();


    void LoadAssets(const std::string& dir, const std::string& filename);
    void LoadAssets(AAssetManager *mgr, const std::string& dir, const std::string& filename);

    void Update(float deltaTimeSeconds);
    void Draw();

    Csm::ICubismModelSetting* GetModelSetting() const { return _modelSetting; }

private:
    std::string _modelDir;
    float _userTimeSeconds = 0.0f;
    Csm::Rendering::CubismRenderer_OpenGLES2* _renderer = nullptr;

    Csm::ICubismModelSetting* _modelSetting = nullptr;
};

#endif // LAPP_MODEL_HPP
