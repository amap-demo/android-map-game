package com.amap.map3d.demo.opengl.ms3dmodel.util;

import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;

//存储系统矩阵状态的类
public class MatrixState {
    private static float[] mProjMatrix = new float[16];// 4x4矩阵 投影用
    private static float[] mVMatrix = new float[16];// 摄像机位置朝向9参数矩阵
    private static float[] currMatrix;// 当前变换矩阵
    private static float[] lightLocation = new float[]{0, 0, 0};// 定位光光源位置
    private static FloatBuffer lightPositionFB;
    private static FloatBuffer cameraFB;
    // 保护变换矩阵的栈
    public static Stack<float[]> mStack = new Stack<float[]>();//保护变换矩阵的栈

    public static void setInitStack() {// 获取不变换初始矩阵
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }

    public static void setChangeMatrix(float[] matrix) {
        currMatrix = matrix.clone();
    }

    public static void pushMatrix() {// 保护变换矩阵
        mStack.push(currMatrix.clone());
    }

    public static void popMatrix()// 恢复变换矩阵
    {
        currMatrix = mStack.pop();
    }

    //插入新矩阵
    public static void setMatrix(float[] mIn) {
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, currMatrix, 0, mIn, 0);
        currMatrix = temp;
    }

    public static void translate(float x, float y, float z) {// 设置沿xyz轴移动
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    public static void rotate(float angle, float x, float y, float z) {// 设置绕xyz轴移动
        Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }

    // 设置摄像机
    static ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
    static float[] cameraLocation = new float[3];// 摄像机位置

    public static void setCamera(float cx, // 摄像机位置x
                                 float cy, // 摄像机位置y
                                 float cz, // 摄像机位置z
                                 float tx, // 摄像机目标点x
                                 float ty, // 摄像机目标点y
                                 float tz, // 摄像机目标点z
                                 float upx, // 摄像机UP向量X分量
                                 float upy, // 摄像机UP向量Y分量
                                 float upz // 摄像机UP向量Z分量
    ) {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);

        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;

        llbb.clear();
        llbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        cameraFB = llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    // 设置透视投影参数
    public static void setProjectFrustum(float left, // near面的left
                                         float right, // near面的right
                                         float bottom, // near面的bottom
                                         float top, // near面的top
                                         float near, // near面距离
                                         float far // far面距离
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    // 设置正交投影参数
    public static void setProjectOrtho(float left, // near面的left
                                       float right, // near面的right
                                       float bottom, // near面的bottom
                                       float top, // near面的top
                                       float near, // near面距离
                                       float far // far面距离
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    // 获取具体物体的总变换矩阵
    static float[] mMVPMatrix = new float[16];

    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix.clone();
    }

	//用于一帧内的摄像机矩阵
	private static float[] mVMatrixForSpecFrame = new float[16];//摄像机位置朝向9参数矩阵
	public static void copyMVMatrix()
	{
		for(int i=0;i<16;i++)
		{
			mVMatrixForSpecFrame[i]=mVMatrix[i];
		}
	}

    // 获取具体物体的变换矩阵
    public static float[] getMMatrix() {
        return currMatrix.clone();
    }

    // 设置灯光位置的方法
    static ByteBuffer llbbL = ByteBuffer.allocateDirect(3 * 4);

    public static void setLightLocation(float x, float y, float z) {
        llbbL.clear();

        lightLocation[0] = x;
        lightLocation[1] = y;
        lightLocation[2] = z;

        llbbL.order(ByteOrder.nativeOrder());// 设置字节顺序
        lightPositionFB = llbbL.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
    }

    public static void LogMatrix() {
        float[] m = new float[16];
        Matrix.orthoM(m, 0, -1, 1, -1, 1, 2, 10);
        Log.d("test1", m[0] + " " + m[4] + " " + m[8] + " " + m[12] + "\n" +
                m[1] + " " + m[5] + " " + m[9] + " " + m[13] + "\n" +
                m[2] + " " + m[6] + " " + m[10] + " " + m[14] + "\n" +
                m[3] + " " + m[7] + " " + m[11] + " " + m[15] + "\n");
    }
}
