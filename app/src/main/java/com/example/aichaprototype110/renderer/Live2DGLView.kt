package com.example.aichaprototype110.renderer

import android.content.Context
import android.opengl.GLSurfaceView

class Live2DGLView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer {

    init {
        // Gunakan OpenGL ES 2.0
        setEGLContextClientVersion(2)
        setRenderer(this)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    /**
     * Dipanggil sekali saat ukuran surface berubah (termasuk pertama kali).
     * Kita inisialisasi Live2D di sini dengan ukuran viewport.
     */
    external fun initNative(width: Int, height: Int)

    /** Dipanggil setiap frame */
    external fun renderNative()

    /** Dipanggil saat view dilepas dari window */
    external fun releaseNative()

    override fun onSurfaceCreated(
        gl: javax.microedition.khronos.opengles.GL10?,
        config: javax.microedition.khronos.egl.EGLConfig?
    ) {
        // Tidak perlu inisialisasi di sini
        initNative(width, height)
    }

    override fun onSurfaceChanged(
        gl: javax.microedition.khronos.opengles.GL10?,
        width: Int,
        height: Int
    ) {
        // Inisialisasi Live2D dengan dimensi surface
        initNative(width, height)
    }

    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
        renderNative()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseNative()
    }

    companion object {
        init {
            System.loadLibrary("aichaprototype110")
        }
    }
}
