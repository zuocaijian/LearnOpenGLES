package com.zcj.test5;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zcj on 2019/6/25 23:08
 * <p>
 * 绘制圆锥
 */
public class Cone {

    private final float[] mVertexPositions;
    private final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final int vertexCount;

    private FloatBuffer mVertexBuffer;
    private int mProgram;
    private int mPostionHandler;
    private int mMatrixHandler;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public Cone() {
        mVertexPositions = createPositions(1.0f);
        vertexCount = mVertexPositions.length / COORDS_PER_VERTEX;
    }

    public void init() {
        ByteBuffer bb = ByteBuffer.allocateDirect(mVertexPositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mVertexPositions);
        mVertexBuffer.position(0);

        int vertexShader = Test5Util.loadShader(Test5Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t5_shader_vertex_cone.glsl");
        int fragmentShader = Test5Util.loadShader(Test5Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t5_shader_fragment_cone.glsl");
        mProgram = Test5Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3.0f, 20.f);
        Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);
        mPostionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPostionHandler);
        GLES20.glVertexAttribPointer(mPostionHandler, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, vertexStride, mVertexBuffer);
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPostionHandler);
    }

    private float[] createPositions(float step) {
        List<Float> data = new ArrayList<>();
        float height = 2.0f;
        float radius = 1.0f;
        data.add(0.0f);
        data.add(0.0f);
        data.add(height);
        for (float i = 0; i < 360f + step; i += step) {
            data.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            data.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            data.add(0.0f);
        }

        float[] positions = new float[data.size()];
        for (int j = 0; j < data.size(); j++) {
            positions[j] = data.get(j);
        }
        return positions;
    }
}
