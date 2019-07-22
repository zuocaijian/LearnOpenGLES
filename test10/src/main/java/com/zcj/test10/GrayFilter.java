package com.zcj.test10;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * @author: cj_zuo
 * Date: 2019/7/22 10:15
 */
public class GrayFilter {
    private int mProgram;
    private int mPositionHandler;
    private int mMatrixHandler;
    private int mCoordinateHandler;
    private int mTextureHandler;

    public GrayFilter() {
        int vertexShader = Test10Util.loadShader(Test10Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t10_shader_vertex_fbo_gray.glsl");
        int fragmentShader = Test10Util.loadShader(Test10Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t10_shader_fragment_fbo_gray.glsl");
        mProgram = Test10Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void draw(FloatBuffer positionBuffer, FloatBuffer coordinateBuffer, float[] mvpMatrix, int textureId) {
        GLES20.glUseProgram(mProgram);
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler, 2, GLES20.GL_FLOAT, false, 0, positionBuffer);
        mCoordinateHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        GLES20.glEnableVertexAttribArray(mCoordinateHandler);
        GLES20.glVertexAttribPointer(mCoordinateHandler, 2, GLES20.GL_FLOAT, false, 0, coordinateBuffer);
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mvpMatrix, 0);
        mTextureHandler = GLES20.glGetUniformLocation(mProgram, "vTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mTextureHandler, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mCoordinateHandler);
        GLES20.glDisableVertexAttribArray(mPositionHandler);
    }
}
