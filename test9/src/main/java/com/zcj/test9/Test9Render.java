package com.zcj.test9;

import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author: cj_zuo
 * Date: 2019/7/5 9:35
 */
public class Test9Render implements GLSurfaceView.Renderer {

    private Camera mCamera;
    private int mBackCameraId;
    private HandlerThread mCameraThread;
    private Handler mHandler;

    private ConditionVariable mCameraCondition = new ConditionVariable();

    public Test9Render() {
        configCameraEnv();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Test9Util.checkGLES20Env("onSurfaceCreated error!");
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        initCamera();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport();

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    private void configCameraEnv() {
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mBackCameraId = i;
                break;
            }
        }

        mCameraThread = new HandlerThread("");
        mCameraThread.start();
        mHandler = new Handler(mCameraThread.getLooper());
    }

    private void initCamera() {
        mHandler.post(() -> {
            mCamera = Camera.open(mBackCameraId);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            parameters.setPreviewFpsRange();
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation();
        });
    }
}
