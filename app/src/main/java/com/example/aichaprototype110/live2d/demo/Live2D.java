package com.example.aichaprototype110.live2d.demo;

public class Live2D {
    static {
        System.loadLibrary("aichaprototype110");
    }

    // called before using live2d library
    public static native void init();

    // called when no longer using live2d library
    public static native void dispose();

    // clear color buffer bit of current frame
    public static native void clearBuffer(float r, float g, float b, float a);

    public static void clearBuffer(float r, float g, float b) {
        clearBuffer(r, g, b, 0.0f);
    }

    public static void clearBuffer(float r, float g) {
        clearBuffer(r, g, 0.0f);
    }

    public static void clearBuffer(float r) {
        clearBuffer(r, 0.0f);
    }

    public static void clearBuffer() {
        clearBuffer(0.0f);
    }

    public static native void glRelease();
}
