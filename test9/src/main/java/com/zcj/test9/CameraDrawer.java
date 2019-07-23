package com.zcj.test9;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author: cj_zuo
 * Date: 2019/7/22 17:07
 */
public class CameraDrawer {

    private final float[] mPositions = {
            -1.0f, 1.0f, //左上角
            -1.0f, -1.0f, //左下角
            1.0f, 1.0f, //右上角
            1.0f, -1.0f //右下角
    };

    private final float[] mCoordinates = {
            0.0f, 1.0f, //左上角
            0.0f, 0.0f, //左下角
            1.0f, 1.0f, //右上角
            1.0f, 0.0f //右下角
    };

    private int mProgram;
    private int mPositionHandler;
    private int mMatrixHandler;
    private int mCoordMatrixHandler;
    private int mCoordinateHandler;
    private int mTextureHandler;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private FloatBuffer mPositionBuffer;
    private FloatBuffer mCoordinateBuffer;

    public void init(int viewWidth, int viewHeight, int textureWidth, int textureHeight) {

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

        int vertexShader = Test9Util.loadShader(Test9Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t9_shader_vertex_camera.glsl");
        int fragmentShader = Test9Util.loadShader(Test9Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t9_shader_fragment_camera.glsl");
        mProgram = Test9Util.createOpenGLESProgram(vertexShader, fragmentShader);

        float ratio = (float) viewWidth / viewHeight;
        float textureRatio = (float) textureWidth / textureHeight;
        //如果希望预览图宽度填充满可视区域的宽，且预览图不变形，则
        //Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -textureRatio / ratio, textureRatio / ratio, 3.0f, 7.0f);
        //如果希望预览图高度填充满可视区域的高，且预览图不变形，则
        //Matrix.orthoM(mProjectionMatrix, 0, -ratio / textureRatio, ratio / textureRatio, -1.0f, 1.0f, 3.0f, 7.0f);

        //在上面两点的基础上，我们希望无论如何预览图能填充满整个可视区域，
        //也即预览图长宽中较小的一边能填充满可视区域相应的一边，预览图长款中较大的一边会被裁剪
        if (textureRatio > ratio) {
            Matrix.orthoM(mProjectionMatrix, 0, -ratio / textureRatio, ratio / textureRatio, -1.0f, 1.0f, 3.0f, 7.0f);
        } else {
            Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -textureRatio / ratio, textureRatio / ratio, 3.0f, 7.0f);
        }

        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.setIdentityM(mModelMatrix, 0);

        float[] tmpMatrix = new float[16];
        Matrix.multiplyMM(tmpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, tmpMatrix, 0, mModelMatrix, 0);
    }

    public void draw(float[] coordMatrix, int textureId) {
        GLES20.glUseProgram(mProgram);
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler, 2, GLES20.GL_FLOAT, false, 2 * 4, mPositionBuffer);
        mCoordinateHandler = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        GLES20.glEnableVertexAttribArray(mCoordinateHandler);
        GLES20.glVertexAttribPointer(mCoordinateHandler, 2, GLES20.GL_FLOAT, false, 2 * 4, mCoordinateBuffer);
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        mCoordMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vCoordMatrix");
        GLES20.glUniformMatrix4fv(mCoordMatrixHandler, 1, false, coordMatrix, 0);
        mTextureHandler = GLES20.glGetUniformLocation(mProgram, "vTexture");
        //激活并绑定外部纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mTextureHandler, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mCoordinateHandler);
        GLES20.glDisableVertexAttribArray(mPositionHandler);
    }
}
