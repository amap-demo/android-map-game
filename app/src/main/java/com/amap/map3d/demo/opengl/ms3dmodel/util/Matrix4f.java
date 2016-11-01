package com.amap.map3d.demo.opengl.ms3dmodel.util;

import android.opengl.Matrix;

//用于计算的4x4矩阵
public class Matrix4f 
{	
	//用于存储矩阵元素的数组
	private float[] matrix = new float[16];		

	//将矩阵设置为单位矩阵
	public final void loadIdentity() 
	{
		Matrix.setIdentityM(matrix, 0);
	}

	//设置平移矩阵参数
	public void setTranslation(Vector3f v) 
	{
		this.matrix[12]=v.getX();
		this.matrix[13]=v.getY();
		this.matrix[14]=v.getZ();
	}	

	//设置旋转矩阵参数，入口参数为欧拉角三个分量组成的三维向量
	//实质上是将欧拉角形式的旋转描述转化为矩阵形式的旋转描述
	public final void genRotationFromEulerAngle(final Vector3f angles) 
	{
		float yaw=angles.getX();
		float pitch=angles.getY();
		float roll=angles.getZ();
		
		final double cr = Math.cos(yaw);
        final double sr = Math.sin(yaw);
        final double cp = Math.cos(pitch);
        final double sp = Math.sin(pitch);
        final double cy = Math.cos(roll);
        final double sy = Math.sin(roll);
        final double srsp = sr * sp;
        final double crsp = cr * sp;
        
        float[] mTemp=new float[16];
        Matrix.setIdentityM(mTemp,0);
        
        mTemp[0] = (float) (cp * cy);
        mTemp[1] = (float) (cp * sy);
        mTemp[2] = (float) (-sp);

        mTemp[4] = (float) (srsp * cy - cr * sy);
        mTemp[5] = (float) (srsp * sy + cr * cy);
        mTemp[6] = (float) (sr * cp);

        mTemp[8] = (float) (crsp * cy + sr * sy);
        mTemp[9] = (float) (crsp * sy - sr * cy);
        mTemp[10] = (float) (cr * cp);
        
        float[] mTempR=new float[16];
        Matrix.multiplyMM(mTempR, 0, this.matrix, 0, mTemp, 0);
        this.matrix=mTempR;
	}
	
	
	//两个矩阵相乘
	public final Matrix4f mul(Matrix4f m1, Matrix4f m2) 
	{		
		float[] mData=new float[16];		
		Matrix.multiplyMM(mData, 0, m1.matrix, 0, m2.matrix,0);		
		this.matrix=mData;
		return this;
    }	
	
	//把一个矩阵的元素值依次拷贝到本矩阵中
	public final void copyFrom(Matrix4f m) 
	{
        for(int i=0; i<16; i++) 
        {
        	this.matrix[i] = m.matrix[i];
        }
    }
	
	//产生旋转矩阵，实际为将四元数形式的旋转描述
	//转化为以矩阵形式描述的旋转
	public final void genRotateFromQuaternion(Vector4f v) 
	{
		float x=v.getX();
		float y=v.getY();
		float z=v.getZ();
		float w=v.getW();
        this.matrix[0] = (1.0f - 2.0f * y * y - 2.0f * z * z);
        this.matrix[1] = (2.0f * (x * y + w * z));
        this.matrix[2] = (2.0f * (x * z - w * y));

        this.matrix[4] = (2.0f * (x * y - w * z));
        this.matrix[5] = (1.0f - 2.0f * x * x - 2.0f * z * z);
        this.matrix[6] = (2.0f * (y * z + w * x));

        this.matrix[8] = (2.0f * (x * z + w * y));
        this.matrix[9] = (2.0f * (y * z - w * x));
        this.matrix[10] = (1.0f - 2.0f * x * x - 2.0f * y * y);

        this.matrix[12] = (float) 0.0;
        this.matrix[13] = (float) 0.0;
        this.matrix[14] = (float) 0.0;

        this.matrix[3] = (float) 0.0;
        this.matrix[7] = (float) 0.0;
        this.matrix[11] = (float) 0.0;
        this.matrix[15] = (float) 1.0;
    }	
	
	//将顶点通过此矩阵进行逆变换得到新的位置 
	public final Vector3f invTransformAndRotate(Vector3f point) 
	{
		Vector3f v = new Vector3f();
        //逆平移部分
        float x = point.getX() - this.matrix[12];
        float y = point.getY() - this.matrix[13];
        float z = point.getZ() - this.matrix[14];
        //逆旋转部分
        v.setX(this.matrix[0] * x + this.matrix[1] * y + this.matrix[2]* z);
        v.setY(this.matrix[4] * x + this.matrix[5] * y + this.matrix[6] * z);
        v.setZ(this.matrix[8] * x + this.matrix[9] * y + this.matrix[10]* z);
        return v;
        
    }
	
	//将顶点通过此矩阵进行变换得到新的位置
	public final Vector3f transform(Vector3f point) 
	{
		Vector3f v = new Vector3f();
        v.setX(
        		this.matrix[0] * point.getX() 
        		+ this.matrix[4] * point.getY() 
        		+ this.matrix[8] * point.getZ()   
        		+ this.matrix[12]);
        v.setY(
        		this.matrix[1] * point.getX() 
        		+ this.matrix[5] * point.getY()   
        		+ this.matrix[9] * point.getZ() 
        		+ this.matrix[13]);  
        v.setZ(
        		this.matrix[2] * point.getX() 
        		+ this.matrix[6] * point.getY() 
        		+ this.matrix[10] * point.getZ() 
        		+ this.matrix[14]);
        return v;
    }
}
