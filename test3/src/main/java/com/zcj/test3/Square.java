package com.zcj.test3;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zcj on 2019/6/20 20:12
 */
public class Square {

    private final float mVertexCoords[] = {
            -0.5f, 0.5f, 0.0f, //top-left
            -0.5f, -0.5f, 0.0f, //bottom-left
            0.5f, -0.5f, 0.0f, //bottom-right
            0.5f, 0.5f, 0.0f //top-right
    };

    private final float mColor[] = {
            1.0f, 1.0f, 1.0f, 1.0f //白色
    };

    private final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final int vertexCount = mVertexCoords.length / COORDS_PER_VERTEX;

    private FloatBuffer mVertexBuffer;
    private int mProgram;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int mPositionHandler;
    private int mColorHandler;
    private int mMatrixHandler;

    public Square() {
    }

    public void init() {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        ByteBuffer bb = ByteBuffer.allocateDirect(mVertexCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mVertexCoords);
        mVertexBuffer.position(0);
        int vertexShader = Test3Util.loadShader(Test3Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t3_shader_vertex_square.glsl");
        int fragmentShader = Test3Util.loadShader(Test3Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t3_shader_fragment_square.glsl");
        mProgram = Test3Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3.0f, 7.0f);
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, mVertexBuffer);
        mColorHandler = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandler, 1, mColor, 0);
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandler);
    }
}
