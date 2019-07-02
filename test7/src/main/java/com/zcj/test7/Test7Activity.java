package com.zcj.test7;

import android.app.Application;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: cj_zuo
 * Date: 2019/6/28 9:47
 */
public class Test7Activity extends AppCompatActivity {

    public static final String TAG = Test7Activity.class.getSimpleName();

    public static Application APP;

    private GLSurfaceView mGLSurfaceView;
    private Test7Render mRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP = getApplication();

        initGLSurfaceView();
        setContentView(mGLSurfaceView);
    }

    private void initGLSurfaceView() {
        mGLSurfaceView = new GLSurfaceView(getBaseContext());
        mRender = new Test7Render();
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mRender);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
