target_sources(${APP_NAME}
  PRIVATE
        AppDelegate.h
        AppDelegate.mm
        LAppAllocator.h
        LAppAllocator.mm
        LAppDefine.h
        LAppDefine.mm
        LAppLive2DManager.h
        LAppLive2DManager.mm
        LAppModel.h
        LAppModel.mm
        LAppPal.h
        LAppPal.mm
        LAppSprite.h
        LAppSprite.mm
        LAppTextureManager.h
        LAppTextureManager.mm
        main.m
        TouchManager.h
        TouchManager.mm
        ViewController.h
        ViewController.mm
        MetalUIView.h
        MetalUIView.m
        MetalView.h
        MetalView.m
        Shaders
        ../../../../../Framework/src/Rendering/Metal/MetalShaders.metal
)

set_source_files_properties(Shaders PROPERTIES MACOSX_PACKAGE_LOCATION Shaders)

set(RESOURCE_FILES
        Shaders
        ../../../../../Framework/src/Rendering/Metal/MetalShaders.metal)

set_target_properties(${APP_NAME} PROPERTIES
  MACOSX_BUNDLE TRUE
  MACOSX_FRAMEWORK_IDENTIFIER org.cmake.ExecutableTarget
  RESOURCE "${RESOURCE_FILES}"
)
