package com.zcj.test5;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: cj_zuo
 * Date: 2019/6/24 13:00
 */
public class Sphere {

    private final float mSpherePositions[];

    private final int COORDS_PER_VERTEX = 3;
    private final int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private FloatBuffer mVertexBuffer;
    private int mProgram;

    private int mPositionHandler;
    private int mMatrixHandler;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public Sphere() {
        mSpherePositions = createPositions(1.0f);
        vertexCount = mSpherePositions.length / COORDS_PER_VERTEX;
    }

    public void init() {
        ByteBuffer bb = ByteBuffer.allocateDirect(mSpherePositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mSpherePositions);
        mVertexBuffer.position(0);

        int vertexShader = Test5Util.loadShader(Test5Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t5_shader_vertex_sphere.glsl");
        int fragmentShader = Test5Util.loadShader(Test5Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t5_shader_fragment_sphere.glsl");
        mProgram = Test5Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3.0f, 15.0f);
        Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -6.0f, -3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, mVertexBuffer);
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandler);
    }

    private float[] createPositions(float step) {
        List<Float> data = new ArrayList<>();
        float r1, r2; //相邻两个纬度圆半径
        float h1, h2; //相邻两个维度高度
        float sin, cos;
        for (float j = -90; j < 90 + step; j += step) {
            r1 = (float) Math.cos(j * Math.PI / 180f);
            r2 = (float) Math.cos((j + step) * Math.PI / 180f);
            h1 = (float) Math.sin(j * Math.PI / 180f);
            h2 = (float) Math.sin((j + step) * Math.PI / 180f);
            for (float k = 0; k < 360 + step; k++) {
                sin = (float) Math.sin(k * Math.PI / 180f);
                cos = (float) Math.cos(k * Math.PI / 180f);
                data.add(r2 * cos);
                data.add(h2);
                data.add(r2 * sin);
                data.add(r1 * cos);
                data.add(h1);
                data.add(r1 * sin);
            }
        }

        float[] positions = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            positions[i] = data.get(i);
        }
        return positions;
    }
}
