package com.zcj.test8;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Stack;

/**
 * @author: cj_zuo
 * Date: 2019/7/4 16:52
 */
public class MatrixOperator {

    private final float[] mVertexPositions = {
            -1.0f, 1.0f, 1.0f, //正面左上角0
            -1.0f, -1.0f, 1.0f, //正面左下角1
            1.0f, -1.0f, 1.0f, //正面右下角2
            1.0f, 1.0f, 1.0f, //正面右上角3
            -1.0f, 1.0f, -1.0f, //反面左上角4
            -1.0f, -1.0f, -1.0f, //反面左下角5
            1.0f, -1.0f, -1.0f, //反面右下角6
            1.0f, 1.0f, -1.0f, //反面右上角7
    };

    private final short[] mIndex = {
            0, 1, 2, 0, 2, 3, //正面
            4, 0, 3, 4, 3, 7,//上面
            4, 5, 6, 4, 6, 7,//反面
            5, 1, 2, 5, 2, 6,//下面
            4, 5, 1, 4, 1, 0,//左侧面
            7, 6, 2, 7, 2, 3//右侧面
    };

    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;
    private int mProgram;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mVPMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int mVertexHandler;
    private int mMatrixHandler;

    public MatrixOperator() {
    }

    public void init() {
        ByteBuffer bb = ByteBuffer.allocateDirect(mVertexPositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mVertexPositions);
        mVertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(mIndex.length * 2);
        bb.order(ByteOrder.nativeOrder());
        mIndexBuffer = bb.asShortBuffer();
        mIndexBuffer.put(mIndex);
        mIndexBuffer.position(0);

        int vertexShader = Test8Util.loadShader(Test8Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t8_shader_vertex_matrix_operator.glsl");
        int fragmentShader = Test8Util.loadShader(Test8Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t8_shader_fragment_matrix_operator.glsl");
        mProgram = Test8Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 20.0f);
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 15.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {
        pushMatrix();
        Matrix.setIdentityM(mModelMatrix, 0);
        calcMVPMatrix();
        drawSelf();
        popMatrix();

        pushMatrix();
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, 60, 1, 2, 1);
        Matrix.translateM(mModelMatrix, 0, 2, 2, -3);
        calcMVPMatrix();
        drawSelf();
        popMatrix();

        pushMatrix();
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, 1.2f, 1.2f, 0.5f);
        Matrix.rotateM(mModelMatrix, 0, 60, -1, -2, 1);
        Matrix.translateM(mModelMatrix, 0, -2, -2, -3);
        calcMVPMatrix();
        drawSelf();
        popMatrix();
    }

    private void drawSelf() {
        GLES20.glUseProgram(mProgram);
        mVertexHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mVertexHandler);
        GLES20.glVertexAttribPointer(mVertexHandler, 3, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);

        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndex.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(mVertexHandler);
    }


    private Stack<float[]> mStack = new Stack<>();      //变换矩阵堆栈

    //保护现场
    public void pushMatrix() {
        mStack.push(Arrays.copyOf(mVPMatrix, 16));
    }

    //恢复现场
    public void popMatrix() {
        mVPMatrix = mStack.pop();
    }

    public void calcMVPMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mModelMatrix, 0);
    }
}
