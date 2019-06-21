package com.zcj.test4;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author: cj_zuo
 * Date: 2019/6/21 14:43
 */
public class Test4Render implements GLSurfaceView.Renderer {

    private Cube mCube;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test4Util.checkGLES20Env("onSurfaceCreated error!");
        mCube = new Cube();
        mCube.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCube.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCube.draw();
    }
}
