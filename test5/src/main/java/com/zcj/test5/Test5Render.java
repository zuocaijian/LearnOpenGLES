package com.zcj.test5;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author: cj_zuo
 * Date: 2019/6/24 12:56
 */
public class Test5Render implements GLSurfaceView.Renderer {

    private Sphere mSphere;
    private Cylinder mCylinder;
    private Cone mCone;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test5Util.checkGLES20Env("onSurfaceCreated error!");
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mSphere = new Sphere();
        mSphere.init();

        mCylinder = new Cylinder();
        mCylinder.init();

        mCone = new Cone();
        mCone.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mSphere.resize(width, height);
        mCylinder.resize(width, height);
        mCone.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSphere.draw();
        mCylinder.draw();
        mCone.draw();
    }
}
