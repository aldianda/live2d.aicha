target_sources(${LIB_NAME}
  PRIVATE
        CubismRenderer.cpp
        CubismRenderer.hpp
        CubismClippingManager.hpp
        CubismClippingManager.tpp
)

# Add specified rendering directory.
add_subdirectory(${FRAMEWORK_SOURCE})

# Add include path set in application (Deprecated).
set(RENDER_INCLUDE_PATH
  ${FRAMEWORK_DX9_INCLUDE_PATH}
  ${FRAMEWORK_DX11_INCLUDE_PATH}
  ${FRAMEWORK_GLEW_PATH}
  ${FRAMEWORK_GLFW_PATH}
  PARENT_SCOPE
)
