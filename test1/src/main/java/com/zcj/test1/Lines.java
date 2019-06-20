package com.zcj.test1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zcj on 2019/6/19 22:21
 * <p>
 * 绘制十字形坐标
 */
public class Lines {

    //OpenGL 坐标系中 十字形 的坐标
    private final float mLinesCoords[] = {
            -1.0f, 0.0f, 0.0f, //west
            1.0f, 0.0f, 0.0f, //east
            0.0f, 1.0f, 0.0f, //north
            0.0f, -1.0f, 0.0f //south
    };

    //顶点单一颜色
    private final float mColor[] = {
            0.0f, 1.0f, 0.0f, 1.0f
    }; //红色

    private final int COORDS_PER_VERTEX = 3; //表示每个顶点坐标用三个数据表示(xyz)
    private final int vertexStride = COORDS_PER_VERTEX * 4; //表示每个顶点3 * 4个字节
    private final int vertexCount = mLinesCoords.length / COORDS_PER_VERTEX; //顶点个数

    private FloatBuffer mVertexBuffer; //传递给OpenGL ES的顶点坐标数据
    private int mProgram; //着色器程序
    private int mPositionHandle; //顶点着色器的vPosition成员句柄
    private int mColorHandle; //片元着色器的vColor成员句柄

    public Lines() {
    }

    public void init() {
        //设置清屏颜色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //申请底层空间
        ByteBuffer bb = ByteBuffer.allocateDirect(mLinesCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //将坐标数据转换为FloatBuffer，用以传递给OpenGL ES程序
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mLinesCoords);
        mVertexBuffer.position(0);
        //加载顶点着色器和片元着色器
        int vertexShader = Test1Util.loadShader(Test1Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t1_shader_vertex_line.glsl");
        int fragmentShader = Test1Util.loadShader(Test1Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t1_shader_fragment_line.glsl");
        //创建着色器程序
        mProgram = Test1Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    public void draw() {
        //调用glClear(GL10.GL_COLOR_BUFFER_BIT)方法清除屏幕颜色,执行这个方法之后
        //屏幕就会渲染之前通过glClearColor设置的清屏颜色.
        //GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);

        //将程序加入到OpenGL ES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用线条顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备线条的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, mVertexBuffer);
        //获取片元着色器vColor成员句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制的线条的颜色
        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);
        //绘制十字形线条
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
