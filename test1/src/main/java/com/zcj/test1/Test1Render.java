package com.zcj.test1;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author: cj_zuo
 * Date: 2019/6/19 16:47
 */
public class Test1Render implements GLSurfaceView.Renderer {

    private Triangle mTriangle;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test1Util.checkGLES20Env("onSurfaceCreated error!");
        mTriangle = new Triangle();
        mTriangle.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mTriangle.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mTriangle.draw();
    }
}
