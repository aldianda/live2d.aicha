package com.example.aichaprototype110.live2d.demo;

import android.content.res.AssetManager;

public class JniBridgeJava {
    // Deklarasi fungsi native yang benar
    public static native void initLive2D();
    public static native void onSurfaceCreated();
    public static native void onSurfaceChanged(int width, int height);
    public static native void onDrawFrame();
    public static native void destroy();
    public static native void setAssetManager(AssetManager assetManager);
    public static native void onTouch(float x, float y);

    static {
        System.loadLibrary("aichaprototype110");
    }
}