package com.zcj.test8;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author: cj_zuo
 * Date: 2019/7/4 16:50
 */
public class Test8Render implements GLSurfaceView.Renderer {

    private MatrixOperator mMatrixOperator;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test8Util.checkGLES20Env("onSurfaceCreated error!");

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mMatrixOperator = new MatrixOperator();
        mMatrixOperator.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mMatrixOperator.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mMatrixOperator.draw();
    }
}
