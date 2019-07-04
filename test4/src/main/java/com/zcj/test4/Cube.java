package com.zcj.test4;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author: cj_zuo
 * Date: 2019/6/21 14:44
 * <p>
 * 绘制立方体
 */
public class Cube {

    private final float mCubePositions[] = {
            -1.0f, 1.0f, 1.0f,      //正面左上0
            -1.0f, -1.0f, 1.0f,     //正面左下1
            1.0f, -1.0f, 1.0f,      //正面右下2
            1.0f, 1.0f, 1.0f,       //正面右上3
            -1.0f, 1.0f, -1.0f,     //反面左上4
            -1.0f, -1.0f, -1.0f,    //反面左下5
            1.0f, -1.0f, -1.0f,     //反面右下6
            1.0f, 1.0f, -1.0f       //反面右上7
    };

    private final short mIndex[] = {
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2     //下面
    };

    //八个顶点颜色，与顶点坐标一一对应
    private final float mColor[] = {
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f
    };

    private final int COORDS_PER_VERTEX = 3;
    private final int COLOR_PER_VERTEX = 4;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private ShortBuffer mIndexBuffer;
    private int mProgram;

    private int mPositionHandler;
    private int mColorHandler;
    private int mMatrixHandler;

    public Cube() {
    }

    public void init() {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        ByteBuffer bb = ByteBuffer.allocateDirect(mCubePositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mCubePositions);
        mVertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(mColor.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mColorBuffer = bb.asFloatBuffer();
        mColorBuffer.put(mColor);
        mColorBuffer.position(0);

        bb = ByteBuffer.allocateDirect(mIndex.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mIndexBuffer = bb.asShortBuffer();
        mIndexBuffer.put(mIndex);
        mIndexBuffer.position(0);

        int vertexShader = Test4Util.loadShader(Test4Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t4_shader_vertex_cube.glsl");
        int fragmentShader = Test4Util.loadShader(Test4Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t4_shader_fragment_cube.glsl");
        mProgram = Test4Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0, 0, 0, 0, 1.0f, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        mColorHandler = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(mColorHandler);
        GLES20.glVertexAttribPointer(mColorHandler, COLOR_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mColorBuffer);
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndex.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandler);
    }
}
