package com.example.aichaprototype110;

import android.opengl.GLSurfaceView;

import com.example.aichaprototype110.live2d.demo.JniBridgeJava;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10; // Perbaiki import


public class GLRenderer implements GLSurfaceView.Renderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) { // Perbaiki parameter (GL10)
        // Dipanggil saat surface OpenGL dibuat
        JniBridgeJava.onSurfaceCreated(); // Perbaiki nama class (Jni bukan Jn1)
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) { // Perbaiki parameter (GL10)
        // Dipanggil saat surface berubah ukuran
        JniBridgeJava.onSurfaceChanged(width, height); // Perbaiki nama class
    }

    @Override
    public void onDrawFrame(GL10 gl) { // Tambahkan method yang hilang
        // Dipanggil setiap frame untuk menggambar
        JniBridgeJava.onDrawFrame();
    }
}