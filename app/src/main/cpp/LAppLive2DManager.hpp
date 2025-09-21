#pragma once

#include <vector>
#include <memory>
#include "LAppModel.hpp"

class LAppLive2DManager {
public:
    static LAppLive2DManager* GetInstance();
    static void ReleaseInstance();

    void Initialize(int width, int height);
    void OnUpdate();
    void Resize(int width, int height);

    LAppLive2DManager();

    ~LAppLive2DManager();

private:

    void LoadModel();

    std::unique_ptr<LAppModel> _model;
    int _screenWidth;
    int _screenHeight;
};
