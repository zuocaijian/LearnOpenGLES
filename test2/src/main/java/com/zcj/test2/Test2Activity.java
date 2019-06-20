package com.zcj.test2;

import android.app.Application;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by zcj on 2019/6/19 23:09
 */
public class Test2Activity extends AppCompatActivity {

    public static final String TAG = Test2Activity.class.getSimpleName();

    public static Application APP;

    private Test2Render mRender;
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP = getApplication();

        initGLSurfaceView();
        setContentView(mGLSurfaceView);
    }

    private void initGLSurfaceView() {
        mGLSurfaceView = new GLSurfaceView(getBaseContext());
        //创建渲染器
        mRender = new Test2Render();
        //设置OpenGL ES版本号，同时创建一个OpenGL ES2.0上下文环境
        mGLSurfaceView.setEGLContextClientVersion(2);
        //绑定渲染器
        mGLSurfaceView.setRenderer(mRender);
        //设置渲染模式
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
