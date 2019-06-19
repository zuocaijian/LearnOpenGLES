package com.zcj.test1;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.InputStream;

/**
 * @author: cj_zuo
 * Date: 2019/6/19 16:40
 */
public class Test1Util {

    public static void checkGLES20Env(String msg) {
        int err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.i(Test1Activity.TAG, msg);
        }
    }

    public static int loadShader(Resources resources, int shaderType, String resName) {
        return loadShader(shaderType, loadFromAssetsFile(resources, resName));
    }

    public static int loadShader(int shaderType, String sourceCode) {
        int shader = -1;
        //创建一个空的着色器
        shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            Log.e(Test1Activity.TAG, "could not create new shader!");
        }
        //设置着色器源码
        GLES20.glShaderSource(shader, sourceCode);
        //编译着色器源码
        GLES20.glCompileShader(shader);
        //检测编译结果
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(Test1Activity.TAG, "Count not compile shader:" + shaderType);
            Log.e(Test1Activity.TAG, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public static int createOpenGLESProgram(int vertexShader, int fragmentShader) {
        int program = -1;
        //创建一个空的OpenGLES程序
        program = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(program, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(program, fragmentShader);
        //链接到着色器程序
        GLES20.glLinkProgram(program);
        //检测链接结果
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(Test1Activity.TAG, "Could not link program:" + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    /**
     * 从assets目录下读取文件内容
     *
     * @param res
     * @param fName
     * @return
     */
    private static String loadFromAssetsFile(Resources res, String fName) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = res.getAssets().open(fName);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                sb.append(new String(buffer, 0, ch));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString().replaceAll("\\r\\n", "\n");
    }
}
