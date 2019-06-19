package com.zcj.test1;

import android.app.Application;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: cj_zuo
 * Date: 2019/6/19 15:28
 */
public class Test1Activity extends AppCompatActivity {

    public static final String TAG = Test1Activity.class.getSimpleName();

    public static Application APP;

    private Test1Render mRender;
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP = getApplication();

        initGLSurfaceView();
        setContentView(mGLSurfaceView);
    }

    private void initGLSurfaceView() {
        mGLSurfaceView = new GLSurfaceView(this);
        //创建渲染器
        mRender = new Test1Render();
        //设置OpenGL ES版本号，同时创建一个 OpenGL ES 2.0 上下文
        mGLSurfaceView.setEGLContextClientVersion(2);
        //绑定渲染器
        mGLSurfaceView.setRenderer(new Test1Render());
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
