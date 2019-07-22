package com.zcj.test9;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * @author: cj_zuo
 * Date: 2019/7/5 9:33
 */
public class Test9Activity extends AppCompatActivity {
    public static final String TAG = Test9Activity.class.getSimpleName();
    private static final int CODE_REQUEST_PERMISSION_CAMERA = 0x10;

    public static Application APP;

    private GLSurfaceView mGLSurfaceView;
    private Test9Render mRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP = getApplication();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initGLSurfaceView();
            setContentView(mGLSurfaceView);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CODE_REQUEST_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQUEST_PERMISSION_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initGLSurfaceView();
            setContentView(mGLSurfaceView);
        }
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initGLSurfaceView();
            setContentView(mGLSurfaceView);
        }*/
    }

    private void initGLSurfaceView() {
        mGLSurfaceView = new GLSurfaceView(getBaseContext());
        mRender = new Test9Render(mGLSurfaceView);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mRender);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
