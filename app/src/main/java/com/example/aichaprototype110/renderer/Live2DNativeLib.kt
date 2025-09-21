package com.example.aichaprototype110.renderer

object Live2DNativeLib {
    init {
        System.loadLibrary("live2d_native") // Sesuai nama .so dari CMake
    }

    external fun init()
    external fun update()
    external fun draw()
}
