package com.example.aichaprototype110.renderer

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class Live2DView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val renderer: Live2DGLView

    init {
        // Gunakan OpenGL ES 2.0
        setEGLContextClientVersion(2)

        renderer = Live2DGLView(context)
        setRenderer(renderer)

        // Atur mode render agar hanya saat diperlukan
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
