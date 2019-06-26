package com.zcj.test5;

import android.app.Application;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: cj_zuo
 * Date: 2019/6/21 16:06
 */
public class Test5Activity extends AppCompatActivity {

    public static final String TAG = Test5Activity.class.getSimpleName();

    public static Application APP;

    private GLSurfaceView mGLSurfaceView;
    private Test5Render mRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP = getApplication();
        initGLSurfaceView();
        setContentView(mGLSurfaceView);
    }

    private void initGLSurfaceView() {
        mGLSurfaceView = new GLSurfaceView(getBaseContext());
        mRender = new Test5Render();
        mGLSurfaceView.setEGLContextClientVersion(2);

        /*//设置GLSurfaceView支持透明通道
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);*/

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
