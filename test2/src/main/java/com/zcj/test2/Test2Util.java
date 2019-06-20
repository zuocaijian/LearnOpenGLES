package com.zcj.test2;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.InputStream;

/**
 * @author: cj_zuo
 * Date: 2019/6/20 9:50
 */
public class Test2Util {

    public static void checkGLES20Env(String msg) {
        int err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.i(Test2Activity.TAG, msg);
        }
    }

    public static int loadShader(Resources res, int shaderType, String fName) {
        return loadShader(shaderType, loadFromAssetsFile(res, fName));
    }

    public static int loadShader(int shaderType, String sourceCode) {
        //创建一个空的着色器
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            Log.e(Test2Activity.TAG, "Could not create new shader!");
        }
        //设置着色器源码
        GLES20.glShaderSource(shader, sourceCode);
        //编译着色器源码
        GLES20.glCompileShader(shader);
        //检测编译结果
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] != GLES20.GL_TRUE) {
            Log.e(Test2Activity.TAG, "Could not compile shader:" + shaderType);
            Log.e(Test2Activity.TAG, "GLES2.0 Error:" + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public static int createOpenGLESProgram(int vertexShader, int fragmentShader) {
        //创建一个空的OpenGLES程序
        int program = GLES20.glCreateProgram();
        //添加顶点着色器到程序
        GLES20.glAttachShader(program, vertexShader);
        //添加片元着色器到程序
        GLES20.glAttachShader(program, fragmentShader);
        //链接到着色器程序
        GLES20.glLinkProgram(program);
        //检测链接结果
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(Test2Activity.TAG, "Could not link program:" + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    private static String loadFromAssetsFile(Resources res, String fName) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = res.getAssets().open(fName);
            int count = 0;
            byte[] buffer = new byte[1024];
            while (-1 != (count = is.read(buffer))) {
                sb.append(new String(buffer, 0, count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().replaceAll("\\r\\n", "\n");
    }
}
