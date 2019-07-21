package com.zcj.test10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zcj on 2019/7/21 10:18
 */
public class Test10Render implements GLSurfaceView.Renderer {

    private FBO mFBO;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test10Util.checkGLES20Env("onSurfaceCreated error!");
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mFBO = new FBO();
        mFBO.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mFBO.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mFBO.draw();
    }
}
