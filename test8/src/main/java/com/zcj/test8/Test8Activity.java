package com.zcj.test8;

import android.app.Application;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: cj_zuo
 * Date: 2019/7/4 16:47
 */
public class Test8Activity extends AppCompatActivity {

    public static final String TAG = Test8Activity.class.getSimpleName();

    public static Application APP;

    private GLSurfaceView mGLSurfaceView;
    private Test8Render mRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP = getApplication();

        initGLSurfaceView();
        setContentView(mGLSurfaceView);
    }

    private void initGLSurfaceView() {
        mGLSurfaceView = new GLSurfaceView(getBaseContext());
        mRender = new Test8Render();
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mRender);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
