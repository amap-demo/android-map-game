package com.amap.map3d.demo.opengl.ms3dmodel.util;

//表示具有三个分量的复合数的类（如顶点位置、法向量等）
public class Vector3f 
{

	float[] coord = new float[3];	//XYZ分量数据数组
	
	public Vector3f() {}
	
	public Vector3f(float x, float y, float z) 
	{
		this.coord[0] = x;
		this.coord[1] = y;
		this.coord[2] = z;
	}

    //两个三维向量按照比例混合
	public final void interpolate(Vector3f v1, Vector3f v2, float alpha) 
	{
		this.setX((1 - alpha) * v1.getX() + alpha * v2.getX());
		this.setY((1 - alpha) * v1.getY() + alpha * v2.getY());
		this.setZ((1 - alpha) * v1.getZ() + alpha * v2.getZ());
	}

	
	public final float[] getVector3fArray() 
	{
		return this.coord;
	}
	
	public final float getX() 
	{
		return this.coord[0];
	}
	
	public final float getY() 
	{
		return this.coord[1];
	}
	
	public final float getZ() 
	{
		return this.coord[2];
	}
	
	public final void setX(float x) 
	{
		this.coord[0] = x;
	}
	
	public final void setY(float y) 
	{
		this.coord[1] = y;
	}
	
	public final void setZ(float z) 
	{
		this.coord[2] = z;
	}
	
}
