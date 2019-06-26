package com.zcj.test6;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zcj on 2019/6/26 22:18
 */
public class Test6Render implements GLSurfaceView.Renderer {

    private ImageTexture mImageTexture;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test6Util.checkGLES20Env("onSurfaceCreated error!");
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mImageTexture = new ImageTexture();
        mImageTexture.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mImageTexture.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mImageTexture.draw();
    }
}
