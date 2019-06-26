package com.zcj.test6;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zcj on 2019/6/26 22:18
 */
public class ImageTexture {

    //顶点坐标
    private final float[] sPos = {
            -1.0f, 1.0f, //左上角
            -1.0f, -1.0f, //左下角
            1.0f, 1.0f, //右上角
            1.0f, -1.0f //右下角
    };

    //顶点对应纹理坐标
    private final float[] sCoord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mCoordBuffer;
    private int mProgram;
    private int mTextureId;
    private int mPositionHandler;
    private int mMatrixHandler;
    private int mCoordHandler;
    private int mTextureHandler;

    private Bitmap mBitmap;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public ImageTexture() {
    }

    public void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(Test6Activity.APP.getResources(), R.drawable.t6_test, options);

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

        int vertexShader = Test6Util.loadShader(Test6Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t6_shader_vertex_texture.glsl");
        int fragmentShader = Test6Util.loadShader(Test6Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t6_shader_fragment_texture.glsl");
        mProgram = Test6Util.createOpenGLESProgram(vertexShader, fragmentShader);

        mTextureId = createTexture();
    }

    public void resize(int width, int height) {
        float ratio = (float) width / height;
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, ratio, 3.0f, 7.0f);

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
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -1.0f / ratio * sWH, 1.0f / ratio * sWH, 3.0f, 7.0f);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -1.0f / ratio, 1.0f / ratio, 3.0f, 7.0f);
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

        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        //传入顶点坐标
        GLES20.glVertexAttribPointer(mPositionHandler, 2, GLES20.GL_FLOAT,
                false, 0, mVertexBuffer);
        mCoordHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        GLES20.glEnableVertexAttribArray(mCoordHandler);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(mCoordHandler, 2, GLES20.GL_FLOAT,
                false, 0, mCoordBuffer);
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //传入变换矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        mTextureHandler = GLES20.glGetUniformLocation(mProgram, "vTexture");
        //激活纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        //传入纹理采样
        GLES20.glUniform1i(mTextureHandler, 0);

        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mPositionHandler);
    }

    /**
     * 创建纹理
     *
     * @return
     */
    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置纹理缩小过滤
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置纹理放大过滤
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
