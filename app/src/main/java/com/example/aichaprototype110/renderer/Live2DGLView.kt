package com.example.aichaprototype110.renderer

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.aichaprototype110.live2d.demo.JniBridgeJava

class Live2DGLView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer {

    init {
        // Use OpenGL ES 2.0
        setEGLContextClientVersion(2)
        setRenderer(this)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onSurfaceCreated(
        gl: javax.microedition.khronos.opengles.GL10?,
        config: javax.microedition.khronos.egl.EGLConfig?
    ) {
        // call into native via bridge
        JniBridgeJava.onSurfaceCreated()
    }

    override fun onSurfaceChanged(
        gl: javax.microedition.khronos.opengles.GL10?,
        width: Int,
        height: Int
    ) {
        // tell native the new viewport size
        JniBridgeJava.onSurfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
        // draw the frame via native
        JniBridgeJava.onDrawFrame()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // release native resources
        JniBridgeJava.destroy()
    }
}
