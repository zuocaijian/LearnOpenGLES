package com.zcj.test10;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.InputStream;

/**
 * Created by zcj on 2019/7/21 10:18
 */
public class Test10Util {
    public static void checkGLES20Env(String msg) {
        int err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.e(Test10Activity.TAG, msg);
        }
    }

    public static int loadShader(Resources res, int shaderType, String fName) {
        return loadShader(shaderType, loadFromAssetsFile(res, fName));
    }

    public static int loadShader(int shaderType, String sourceCode) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shaderType <= 0) {
            Log.e(Test10Activity.TAG, "Could not create a new shader!");
        }
        GLES20.glShaderSource(shader, sourceCode);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] != GLES20.GL_TRUE) {
            Log.e(Test10Activity.TAG, "Could not compile shader: " + shader);
            Log.e(Test10Activity.TAG, "GLES20 Error: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public static int createOpenGLESProgram(int vertexShader, int fragmentShader) {
        int program = GLES20.glCreateProgram();
        if (program <= 0) {
            Log.e(Test10Activity.TAG, "Could not create a new program!");
        }
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(Test10Activity.TAG, "Could not link program: " + program);
            Log.e(Test10Activity.TAG, "GLES20 Error: " + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    private static String loadFromAssetsFile(Resources res, String fName) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = res.getAssets().open(fName);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) != 0) {
                sb.append(new String(buffer, 0, count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().trim().replaceAll("\\r\\n", "\n");
    }
}
