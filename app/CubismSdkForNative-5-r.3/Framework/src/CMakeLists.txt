target_sources(${LIB_NAME}
  PRIVATE
        CubismCdiJson.cpp
        CubismCdiJson.hpp
        CubismDefaultParameterId.cpp
        CubismDefaultParameterId.hpp
        CubismFramework.cpp
        CubismFramework.hpp
        CubismFrameworkConfig.hpp
        ../../../src/main/cpp/CubismModelSettingJson.cpp
        ../../../src/main/cpp/CubismModelSettingJson.hpp
        CubismJsonHolder.hpp
        ICubismAllocator.hpp
        ICubismModelSetting.hpp
        Live2DCubismCore.hpp
)

# Add sub directories.
add_subdirectory(Effect)
add_subdirectory(Id)
add_subdirectory(Math)
add_subdirectory(Model)
add_subdirectory(Motion)
add_subdirectory(Physics)
add_subdirectory(Rendering)
add_subdirectory(Type)
add_subdirectory(Utils)
