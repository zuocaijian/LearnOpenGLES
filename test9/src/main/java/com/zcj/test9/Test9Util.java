package com.zcj.test9;

import android.content.res.Resources;
import android.opengl.GLES10;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author: cj_zuo
 * Date: 2019/7/5 9:36
 */
public class Test9Util {

    public static void checkGLES20Env(String msg) {
        int err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.e(Test9Activity.TAG, msg);
        }
    }

    public static int loadShader(Resources res, int shaderType, String fName) {
        return loadShader(shaderType, loadFromAssetsFile(res, fName));
    }

    public static int loadShader(int shaderType, String sourceCode) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            Log.e(Test9Activity.TAG, "Could not create new shader!");
        }
        GLES20.glShaderSource(shader, sourceCode);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] != GLES20.GL_TRUE) {
            Log.e(Test9Activity.TAG, "Could not compile shader: " + shader);
            Log.e(Test9Activity.TAG, "GLES20 Error: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public static int createOpenGLESProgram(int vertexShader, int fragmentShader) {
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            Log.e(Test9Activity.TAG, "Could not create new program!");
        }
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(Test9Activity.TAG, "Could not link program: " + program);
            Log.e(Test9Activity.TAG, "GLES20 Error: " + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    public static int createTextureId() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES10.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES10.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES10.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    private static String loadFromAssetsFile(Resources res, String fName) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = res.getAssets().open(fName);
            int count = 0;
            byte[] buffer = new byte[1024];
            while ((count = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().replaceAll("\\r\\n", "\n");
    }
}
