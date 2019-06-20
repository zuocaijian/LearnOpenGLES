package com.zcj.test3;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zcj on 2019/6/20 20:08
 */
public class Test3Render implements GLSurfaceView.Renderer {

    private Square mSquare;
    private Circle mCircle;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test3Util.checkGLES20Env("onSurfaceCreated error!");
        mSquare = new Square();
        mSquare.init();

        mCircle = new Circle();
        mCircle.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mSquare.resize(width, height);
        mCircle.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSquare.draw();
        mCircle.draw();
    }
}
