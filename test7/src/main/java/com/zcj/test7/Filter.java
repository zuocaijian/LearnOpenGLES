package com.zcj.test7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author: cj_zuo
 * Date: 2019/6/28 15:27
 */
public class Filter {

    private FilterType mFilterType;

    //顶点坐标
    private final float[] sPos = {
            -1.0f, 1.0f, //左上角
            -1.0f, -1.0f, //左下角
            1.0f, 1.0f, //右上角
            1.0f, -1.0f //右下角
    };

    //顶点对应纹理坐标
    private final float[] sCoord = {
            0.0f, 0.0f, //左上角
            0.0f, 1.0f, //左下角
            1.0f, 0.0f, //右上角
            1.0f, 1.0f //右下角
    };

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mCoordBuffer;
    private int mTextureId;
    private int mProgram;
    private int mPositionHandler;
    private int mMatrixHandler;
    private int mCoordHandler;
    private int mTextureHandler;

    private Bitmap mBitmap;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public enum FilterType {
        NONE(0, new float[]{0.0f, 0.0f, 0.0f}),
        GRAY(1, new float[]{0.299f, 0.587f, 0.114f}),
        COOL(2, new float[]{0.0f, 0.0f, 0.1f}),
        WRAM(2, new float[]{0.1f, 0.0f, 0.0f}),
        BLUR(3, new float[]{0.006f, 0.004f, 0.002f}),
        MAGN(4, new float[]{0.0f, 0.0f, 0.4f});

        private int changeType;
        private float[] data;

        FilterType(int changeType, float[] data) {
            this.changeType = changeType;
            this.data = data;
        }
    }

    public Filter(FilterType filterType) {
        this.mFilterType = filterType;
    }

    public void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(Test7Activity.APP.getResources(), R.drawable.t7_test, options);

        ByteBuffer bb = ByteBuffer.allocateDirect(sPos.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(sPos);
        mVertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(sCoord.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mCoordBuffer = bb.asFloatBuffer();
        mCoordBuffer.put(sCoord);
        mCoordBuffer.position(0);

        int vertexShader = Test7Util.loadShader(Test7Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t7_shader_vertex_filter.glsl");
        int fragmentShader = Test7Util.loadShader(Test7Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t7_shader_fragment_filter.glsl");
        mProgram = Test7Util.createOpenGLESProgram(vertexShader, fragmentShader);

        mTextureId = createTexture();
    }

    private float ratio;

    public void resize(int width, int height) {
        ratio = (float) width / height;

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;

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
        Matrix.setIdentityM(mModelMatrix, 0);

        float[] tmpMatrix = new float[16];
        Matrix.multiplyMM(tmpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, tmpMatrix, 0, mModelMatrix, 0);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);
        int ratioHandler = GLES20.glGetUniformLocation(mProgram, "uXY");
        GLES20.glUniform1f(ratioHandler, ratio);
        int isHalfHandler = GLES20.glGetUniformLocation(mProgram, "vIsHalf");
        GLES20.glUniform1i(isHalfHandler, 0);
        int changeTypeHandler = GLES20.glGetUniformLocation(mProgram, "vChangeType");
        GLES20.glUniform1i(changeTypeHandler, mFilterType.changeType);
        int changeColorHandler = GLES20.glGetUniformLocation(mProgram, "vChangeColor");
        GLES20.glUniform3fv(changeColorHandler, 1, mFilterType.data, 0);

        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler, 2, GLES20.GL_FLOAT,
                false, 0, mVertexBuffer);
        mCoordHandler = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        GLES20.glEnableVertexAttribArray(mCoordHandler);
        GLES20.glVertexAttribPointer(mCoordHandler, 2, GLES20.GL_FLOAT,
                false, 0, mCoordBuffer);
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        mTextureHandler = GLES20.glGetUniformLocation(mProgram, "vTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mTextureHandler, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mPositionHandler);
        GLES20.glDisableVertexAttribArray(mCoordHandler);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
