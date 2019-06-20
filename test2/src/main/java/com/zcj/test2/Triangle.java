package com.zcj.test2;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author: cj_zuo
 * Date: 2019/6/20 10:41
 * <p>
 * 绘制等腰三角形
 * 需要用到M(Model)V(View)P(Projection)矩阵来完成坐标变换。
 */
public class Triangle {

    //OpenGL 坐标系中 直角等腰三角形 的坐标
    private final float mTriangleCoords[] = {
            0.5f, 0.5f, 0.0f, //top
            -0.5f, -0.5f, 0.0f, //bottom left
            0.5f, -0.5f, 0.0f //bottom right
    };

    //顶点单一颜色
    private final float mColor[] = {
            1.0f, 1.0f, 1.0f, 1.0f
    }; //白色

    private final int COORDS_PER_VERTEX = 3; //表示每个顶点坐标用三个数据表示(xyz)
    private final int vertexStride = COORDS_PER_VERTEX * 4; //表示每个顶点3 * 4个字节
    private final int vertexCount = mTriangleCoords.length / COORDS_PER_VERTEX; //顶点的个数

    private FloatBuffer mVertexBuffer; //传递给OpenGL ES的顶点坐标数据
    private int mProgram; //着色器程序
    private int mPositionHandle; //顶点着色器的vPosition成员句柄
    private int mColorHandle; //片元着色器的vColor成员句柄
    private int mMatrixHandle; //顶点着色器的vMatrix成员句柄

    private float[] mProjectMatrix = new float[16]; //投影矩阵P
    private float[] mViewMatrix = new float[16]; //观察矩阵V
    private float[] mMVPMatrix = new float[16]; //变换矩阵MVP

    public Triangle() {
    }

    public void init() {
        //设置清屏颜色(背景色)
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //申请底层空间
        ByteBuffer bb = ByteBuffer.allocateDirect(mTriangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mTriangleCoords);
        mVertexBuffer.position(0);
        //加载顶点着色器
        int vertexShader = Test2Util.loadShader(Test2Activity.APP.getResources(), GLES20.GL_VERTEX_SHADER, "t2_shader_vertex_triangle.glsl");
        //加载片元着色器
        int fragmentShader = Test2Util.loadShader(Test2Activity.APP.getResources(), GLES20.GL_FRAGMENT_SHADER, "t2_shader_fragment_triangle.glsl");
        //创建着色器程序
        mProgram = Test2Util.createOpenGLESProgram(vertexShader, fragmentShader);
    }

    public void resize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        //设置观察矩阵V和投影矩阵P，然后用观察矩阵与投影矩阵相乘，得到实际变换矩阵
        //计算宽高比
        float ratio = (float) width / height;
        //设置透射投影矩阵
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置观察矩阵
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3.0f, 0, 0, 0, 0, 1.0f, 0);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {
        //调用glClear(GL10.GL_COLOR_BUFFER_BIT)方法清除屏幕颜色，执行这个方法之后
        //屏幕就会渲染之前通过glClearColor设置的清屏颜色
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);

        //将程序加入到OpenGL ES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取顶点着色器的变换矩阵的句柄
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //设置变换矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        //获取顶点着色器的vPosition的句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, mVertexBuffer);
        //获取片元着色器vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
