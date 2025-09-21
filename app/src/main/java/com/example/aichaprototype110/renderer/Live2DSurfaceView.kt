package com.example.aichaprototype110.renderer

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView

class Live2DSurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Nanti kita panggil native code C++ untuk init dan render Live2D di sini
        Live2DNativeLib.init()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle resizing atau perubahan ukuran surface
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Bersihkan resource jika diperlukan
    }
}
