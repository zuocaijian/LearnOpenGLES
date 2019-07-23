package com.zcj.test9;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.io.IOException;
import java.util.concurrent.Semaphore;

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

    private Semaphore mSemaphore = new Semaphore(1);
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;

    private Thread mSurfaceTextureAvailableThread;
    private Handler mSurfaceTextureAvailableHandler;

    private GLSurfaceView mGLSurfaceView;

    private CameraDrawer mCameraDrawer;
    private float[] mSurfaceTextureMatrix = new float[16];

    public Test9Render(GLSurfaceView glSurfaceView) {
        this.mGLSurfaceView = glSurfaceView;
        configCameraEnv();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //1、检查OpenGL环境、设置OpenGL的背景色等；
        //2、在相机操作线程中初始化相机：获取摄像头句柄，设置预览尺寸、对焦、闪光灯等模式；
        //3、待相机初始化完成后再进行后续操作，如创建扩展纹理、创建SurfaceTexture等
        Test9Util.checkGLES20Env("onSurfaceCreated error!");
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        try {
            mSemaphore.acquire();
            cameraInit();
            mSemaphore.acquire();
            surfaceCreated();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //1、用创建好的SurfaceTexture作为相机的预览，在相机操作线程中打开摄像头
        //2、根据GLSurfaceView的可视区域大小，设置相机的最佳预览尺寸和拍照尺寸
        //3、等待摄像头打开后进行后续操作，如计算变换矩阵等
        GLES20.glViewport(0, 0, width, height);
        try {
            mSemaphore.acquire();
            cameraStart(width, height);
            mSemaphore.acquire();
            surfaceChanged(width, height);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurfaceTexture.getTransformMatrix(mSurfaceTextureMatrix);
        mCameraDrawer.draw(mSurfaceTextureMatrix, mTextureId);
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

        mCameraThread = new HandlerThread("cameraThread");
        mCameraThread.start();
        mHandler = new Handler(mCameraThread.getLooper());
    }

    private void cameraInit() {
        mHandler.post(() -> {
            mCamera = Camera.open(mBackCameraId);
            Camera.Parameters parameters = mCamera.getParameters();
            //parameters.setPreviewSize();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            //parameters.setPreviewFpsRange();
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            mSemaphore.release();
        });
    }

    private void cameraStart(int width, int height) {
        mHandler.post(() -> {
            try {
                CameraUtil.setPropSize(mCamera, width, height);
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
                mSemaphore.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void surfaceCreated() {
        mCameraDrawer = new CameraDrawer();
        mTextureId = Test9Util.createTextureId();
        mSurfaceTextureAvailableThread = new HandlerThread("SurfaceTextureAvailable");
        mSurfaceTextureAvailableThread.start();
        mSurfaceTextureAvailableHandler = new Handler(((HandlerThread) mSurfaceTextureAvailableThread).getLooper());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSurfaceTexture = new SurfaceTexture(mTextureId);
            mSurfaceTexture.setOnFrameAvailableListener(new Test9OnFrameAvailableListener(), mSurfaceTextureAvailableHandler);
            mSemaphore.release();
        } else {
            Semaphore semaphore = new Semaphore(1);
            try {
                semaphore.acquire();
                mSurfaceTextureAvailableThread = new Thread("SurfaceTextureAvailable") {
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();
                        synchronized (this) {
                            mSurfaceTexture = new SurfaceTexture(mTextureId);
                            mSurfaceTexture.setOnFrameAvailableListener(new Test9OnFrameAvailableListener());
                            notifyAll();
                            semaphore.release();
                            mSemaphore.release();
                        }
                        Looper.loop();
                    }
                };
                mSurfaceTextureAvailableThread.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void surfaceChanged(int width, int height) {
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        mCameraDrawer.init(width, height, previewSize.height, previewSize.width);
        mSemaphore.release();
        mGLSurfaceView.queueEvent(() -> mSurfaceTexture.updateTexImage());
    }

    private class Test9OnFrameAvailableListener implements SurfaceTexture.OnFrameAvailableListener {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            //本应该在此处调用GLSurfaceView.requestRender()来回调到GLThread的onDrawFrame()方法，
            //然后在GLThread的onDrawFrame()方法内调用mSurfaceTexture.updateTexImage()来更新纹理，
            //最后将更新好的纹理渲染到屏幕上；
            //但此处为了模仿离屏渲染等操作，通过线程间通讯，先在GLThread线程内调用mSurfaceTexture.updateTexImage()来更新纹理，
            //同时在在GLThread线程内对纹理做离屏渲染等操作
            //最后调用GLSurfaceView.requestRender()通知刷新
            mGLSurfaceView.queueEvent(() -> {
                mSurfaceTexture.updateTexImage();
                // TODO: 2019/7/22 离屏渲染等操作
                mGLSurfaceView.requestRender();
            });
        }
    }
}
