cmake_minimum_required(VERSION 3.4.1)
project(aichaprototype110 LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
add_definitions(-DCSM_PLATFORM_ANDROID)

include_directories(
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src
        ${CMAKE_SOURCE_DIR}/src/main/cpp
)

file(GLOB_RECURSE CUBISM_FRAMEWORK_SRC
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Type/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Math/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Model/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Utils/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Effect/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Motion/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Expression/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Physics/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/EyeBlink/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Pose/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Id/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/CubismFramework.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Effect/*.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Rendering/CubismRenderer.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Rendering/CubismClippingManager.cpp
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Framework/src/Rendering/OpenGL/*.cpp
)

# 4) Buat static lib untuk Framework
add_library(cubism_framework STATIC ${CUBISM_FRAMEWORK_SRC})

# 5) Daftar semua .cpp milikmu (shared lib)
set(APP_SRC
        src/main/cpp/aichaprototype110.cpp
        src/main/cpp/Live2DRenderer.cpp
        src/main/cpp/LAppLive2DManager.cpp
        src/main/cpp/LAppAllocator.cpp
        src/main/cpp/LAppModel.cpp
        src/main/cpp/native-lib.cpp
        src/main/cpp/LAppPal.cpp
        src/main/cpp/CubismModelSettingJson.cpp
)

add_library(aichaprototype110 SHARED ${APP_SRC})

add_library(Live2DCubismCore STATIC IMPORTED)
set_target_properties(Live2DCubismCore PROPERTIES
        IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/CubismSdkForNative-5-r.3/Core/lib/android/${ANDROID_ABI}/libLive2DCubismCore.a
)


find_library(log-lib log)
find_library(gles-lib GLESv2)
find_library(android-lib android)


target_link_libraries(
        aichaprototype110
        cubism_framework
        Live2DCubismCore
        ${log-lib}
        ${gles-lib}
        ${android-lib}
        c++_shared
)
