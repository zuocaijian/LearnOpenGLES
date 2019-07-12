package com.zcj.test4;

import android.app.Application;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: cj_zuo
 * Date: 2019/6/21 14:30
 */
public class Test4Activity extends AppCompatActivity {

    public static final String TAG = Test4Activity.class.getSimpleName();

    public static Application APP;

    private GLSurfaceView mGLSurfaceView;
    private Test4Render mRender;

    private TextView mTvEye;
    private TextView mTvCenter;
    private TextView mTvUp;
    private float mEyeX = 0.0f, mEyeY = 0.0f, mEyeZ = 10.0f;
    private float mCenterX = 0.0f, mCenterY = 0.0f, mCenterZ = 0.0f;
    private float mUpX = 0.0f, mUpY = 1.0f, mUpZ = 0.0f;
    private final float STEP = 0.5f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP = getApplication();

        initGLSurfaceView();
        setContentView(R.layout.activity_test_4);

        FrameLayout fl = findViewById(R.id.fl_container);
        fl.addView(mGLSurfaceView);

        setAction();
    }

    private void initGLSurfaceView() {
        mGLSurfaceView = new GLSurfaceView(getBaseContext());
        mRender = new Test4Render();
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mRender);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private void setAction() {
        Action action = new Action();
        mTvEye = findViewById(R.id.tv_eye);
        mTvCenter = findViewById(R.id.tv_center);
        mTvUp = findViewById(R.id.tv_up);

        //eye
        findViewById(R.id.tv_eye_x_add).setOnClickListener(action);
        findViewById(R.id.tv_eye_x_sub).setOnClickListener(action);
        findViewById(R.id.tv_eye_y_add).setOnClickListener(action);
        findViewById(R.id.tv_eye_y_sub).setOnClickListener(action);
        findViewById(R.id.tv_eye_z_add).setOnClickListener(action);
        findViewById(R.id.tv_eye_z_sub).setOnClickListener(action);
        //center
        findViewById(R.id.tv_center_x_add).setOnClickListener(action);
        findViewById(R.id.tv_center_x_sub).setOnClickListener(action);
        findViewById(R.id.tv_center_y_add).setOnClickListener(action);
        findViewById(R.id.tv_center_y_sub).setOnClickListener(action);
        findViewById(R.id.tv_center_z_add).setOnClickListener(action);
        findViewById(R.id.tv_center_z_sub).setOnClickListener(action);
        //up
        findViewById(R.id.tv_up_x_add).setOnClickListener(action);
        findViewById(R.id.tv_up_x_sub).setOnClickListener(action);
        findViewById(R.id.tv_up_y_add).setOnClickListener(action);
        findViewById(R.id.tv_up_y_sub).setOnClickListener(action);
        findViewById(R.id.tv_up_z_add).setOnClickListener(action);
        findViewById(R.id.tv_up_z_sub).setOnClickListener(action);
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

    private class Action implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_eye_x_add) {
                mEyeX += STEP;
            } else if (v.getId() == R.id.tv_eye_x_sub) {
                mEyeX -= STEP;
            } else if (v.getId() == R.id.tv_eye_y_add) {
                mEyeY += STEP;
            } else if (v.getId() == R.id.tv_eye_y_sub) {
                mEyeY -= STEP;
            } else if (v.getId() == R.id.tv_eye_z_add) {
                mEyeZ += STEP;
            } else if (v.getId() == R.id.tv_eye_z_sub) {
                mEyeZ -= STEP;
            } else if (v.getId() == R.id.tv_center_x_add) {
                mCenterX += STEP;
            } else if (v.getId() == R.id.tv_center_x_sub) {
                mCenterX -= STEP;
            } else if (v.getId() == R.id.tv_center_y_add) {
                mCenterY += STEP;
            } else if (v.getId() == R.id.tv_center_y_sub) {
                mCenterY -= STEP;
            } else if (v.getId() == R.id.tv_center_z_add) {
                mCenterZ += STEP;
            } else if (v.getId() == R.id.tv_center_z_sub) {
                mCenterZ -= STEP;
            } else if (v.getId() == R.id.tv_up_x_add) {
                mUpX += STEP;
            } else if (v.getId() == R.id.tv_up_x_sub) {
                mUpX -= STEP;
            } else if (v.getId() == R.id.tv_up_y_add) {
                mUpY += STEP;
            } else if (v.getId() == R.id.tv_up_y_sub) {
                mUpY -= STEP;
            } else if (v.getId() == R.id.tv_up_z_add) {
                mUpZ += STEP;
            } else if (v.getId() == R.id.tv_up_z_sub) {
                mUpZ -= STEP;
            }
            mRender.setLookAt(mEyeX, mEyeY, mEyeZ,
                    mCenterX, mCenterY, mCenterZ,
                    mUpX, mUpY, mUpZ);
            mGLSurfaceView.requestRender();
            mTvEye.setText(mEyeX + ",  " + mEyeY + ", " + mEyeZ);
            mTvCenter.setText(mCenterX + ",  " + mCenterY + ", " + mCenterZ);
            mTvUp.setText(mUpX + ",  " + mUpY + ", " + mUpZ);
        }
    }
}
