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
 * Date: 2019/6/25 18:15
 */
public class Cylinder {

    private final float[] mCylinderPositions;
    private final int COORDS_PER_VERTEX = 3;

    private FloatBuffer mVertexBuffer;
    private int mProgram;
    private int mPositionHandler;
    private int mMatrixHandler;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public Cylinder() {
        mCylinderPositions = createPositions(1.0f);
    }

    public void init() {
        ByteBuffer bb = ByteBuffer.allocateDirect(mCylinderPositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mCylinderPositions);
        mVertexBuffer.position(0);

        int vertexShader = Test5Util.loadShader(Test5Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t5_shader_vertex_cylinder.glsl");
        int fragmentShader = Test5Util.loadShader(Test5Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t5_shader_fragment_cylinder.glsl");
        mProgram = Test5Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3.0f, 10.f);
        Matrix.setLookAtM(mViewMatrix, 0, 2.0f, 2.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {

    }

    private float[] createPositions(float step) {
        List<Float> data = new ArrayList<>();
        float radius = 1.0f;
        float height = 2.0f;
        for (float i = 0; i < 360f + step; i += step) {
            data.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            data.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            data.add(height);
            data.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            data.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            data.add(0f);
        }
        float[] positions = new float[data.size()];
        for (int j = 0; j < data.size(); j++) {
            positions[j] = data.get(j);
        }
        return positions;
    }
}
