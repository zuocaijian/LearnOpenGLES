package com.zcj.test10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zcj on 2019/7/21 17:18
 * 实现两个功能：
 * 1、在屏幕上渲染正常的图片纹理
 * 2、离屏渲染图片纹理，并对图片纹理做灰度处理，最后保存
 */
public class FBO {

    //顶点坐标
    private final float[] mPositions = {
            -1.0f, 1.0f, //左上角
            -1.0f, -1.0f, //左下角
            1.0f, 1.0f, //右上角
            1.0f, -1.0f //右下角
    };

    //纹理坐标
    private final float[] mCoordinates = {
            0.0f, 0.0f, //左上角
            0.0f, 1.0f, //左下角
            1.0f, 0.0f, //右上角
            1.0f, 1.0f //右下角
    };

    private int mProgram;
    private int mPositionHandler;
    private int mMaxtrixHandler;
    private int mCoordinateHandler;
    private FloatBuffer mPositionBuffer;
    private FloatBuffer mCoordinateBuffer;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private Bitmap mBitmap;

    private int[] mFrame = new int[1];
    private int[] mRender = new int[1];
    private int[] mTexutre = new int[2];

    public FBO() {
    }

    public void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(Test10Activity.APP.getResources(), R.drawable.t10_test, options);

        ByteBuffer bb = ByteBuffer.allocateDirect(mPositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mPositionBuffer = bb.asFloatBuffer();
        mPositionBuffer.put(mPositions);
        mPositionBuffer.position(0);

        bb = ByteBuffer.allocateDirect(mCoordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mCoordinateBuffer = bb.asFloatBuffer();
        mCoordinateBuffer.put(mCoordinates);
        mCoordinateBuffer.position(0);

        int vertexShader = Test10Util.loadShader(Test10Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "");
        int fragmentShader = Test10Util.loadShader(Test10Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "");
        mProgram = Test10Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        float ratio = (float) width / height;
        //Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3.0f, 7.0f );
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = (float) w / h;

        if (width > height) {
            if (sWH > ratio) {
                Matrix.orthoM(mProjectionMatrix, 0, -ratio * sWH, ratio * sWH, -1.0f, 1.0f, 3.0f, 7.0f);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f);
            }
        } else {
            if (sWH > ratio) {
                Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -1.0f / ratio * sWH, 1.0f / ratio * sWH, 3.0f, 7.0f);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -1.0f / ratio, 1.0f / ratio, 3.0f, 7.0f);
            }
        }

        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.setIdentityM(mViewMatrix, 0);

        float[] tmpMatrix = new float[16];
        Matrix.multiplyMM(tmpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, tmpMatrix, 0, mModelMatrix, 0);
    }

    public void draw() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            createEnv();
            //绑定帧缓冲
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrame[0]);
            //把纹理缓冲挂在到帧缓冲(存储颜色)
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTexutre[1], 0);
            //把渲染缓冲挂载到帧缓冲(存储深度)
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mRender[0]);
            //绑定帧缓冲后的绘制会绘制到mTexture[1]
            GLES20.glViewport(0, 0, mBitmap.getWidth(), mBitmap.getHeight());

            mBitmap.recycle();
        }
        deleteEnv();
    }

    private void createEnv() {
        //创建帧缓冲
        GLES20.glGenFramebuffers(1, mFrame, 0);
        //创建渲染缓冲
        GLES20.glGenRenderbuffers(1, mRender, 0);
        //绑定渲染缓冲
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRender[0]);
        //设置为深度的渲染缓冲并设置大小
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, mBitmap.getWidth(), mBitmap.getHeight());
        //把渲染缓冲挂在到帧缓冲（存储深度）
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mRender[0]);
        //解绑渲染缓冲.(绑定到默认渲染缓冲，即输出到屏幕)
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        //创建图片纹理
        createTexture();
    }

    private void createTexture() {
        GLES20.glGenTextures(2, mTexutre, 0);
        for (int i = 0; i < 2; i++) {
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexutre[i]);
            //设置纹理参数
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据参数生成纹理
            if (i == 0) {
                //生成图片纹理
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            } else {
                //生成一个空的图片纹理，大小与图片一致
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(),
                        0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            }
        }
    }

    private void deleteEnv() {
        GLES20.glDeleteTextures(2, mTexutre, 0);
        GLES20.glDeleteRenderbuffers(1, mRender, 0);
        GLES20.glDeleteFramebuffers(1, mFrame, 0);
    }
}
