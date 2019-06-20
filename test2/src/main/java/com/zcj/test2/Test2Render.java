package com.zcj.test2;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author: cj_zuo
 * Date: 2019/6/20 10:07
 */
public class Test2Render implements GLSurfaceView.Renderer {

    private Triangle mTriangle;
    private ColorfulTriangle mColorfulTriangle;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test2Util.checkGLES20Env("onSurfaceCreated error!");
        mTriangle = new Triangle();
        mTriangle.init();

        mColorfulTriangle = new ColorfulTriangle();
        mColorfulTriangle.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mTriangle.resize(width, height);
        mColorfulTriangle.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mTriangle.draw();
        mColorfulTriangle.draw();
    }
}
