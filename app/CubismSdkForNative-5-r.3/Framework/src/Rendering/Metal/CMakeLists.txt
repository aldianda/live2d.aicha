target_sources(${LIB_NAME}
  PRIVATE
        CubismOffscreenSurface_Metal.mm
        CubismOffscreenSurface_Metal.hpp
        CubismRenderer_Metal.mm
        CubismRenderer_Metal.hpp
        CubismCommandBuffer_Metal.mm
        CubismCommandBuffer_Metal.hpp
        CubismShader_Metal.mm
        CubismShader_Metal.hpp
        CubismRenderingInstanceSingleton_Metal.m
        CubismRenderingInstanceSingleton_Metal.h
        MetalShaderTypes.h
)

# Disable ARC
set_target_properties(
    ${LIB_NAME}
    PROPERTIES
    MACOSX_BUNDLE YES
    XCODE_ATTRIBUTE_CLANG_ENABLE_OBJC_ARC NO
)
