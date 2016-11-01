package com.amap.map3d.demo.opengl.ms3dmodel.util;

//表示具有四个分量的数据（如齐次坐标、四元数等）
public class Vector4f 
{
	//存放四个分量值的数组
	float[] coording = new float[4];
	
	//设置旋转数据，入口参数为欧拉角的三个分量
	//转换为四元数的四个分量XYZW
	public final void setFromEulerAngleToQuaternion(float yaw, float pitch, float roll) 
	{
		float angle = 0.0f;
        float sr, sp, sy, cr, cp, cy;
        angle = yaw * 0.5f;
        sr = (float) Math.sin(angle);
        cr = (float) Math.cos(angle);
        angle = pitch * 0.5f;
        sp = (float) Math.sin(angle);
        cp = (float) Math.cos(angle);
        angle = roll * 0.5f;
        sy = (float) Math.sin(angle);
        cy = (float) Math.cos(angle);       

        this.setX(sr * cp * cy - cr * sp * sy); // X
        this.setY(cr * sp * cy + sr * cp * sy); // Y
        this.setZ(cr * cp * sy - sr * sp * cy); // Z
        this.setW(cr * cp * cy + sr * sp * sy); // W
	}
	
	//实施v1与v2两个四元数按照alpha与1-alpha的比例融合插值
	public final void interpolate(Vector4f v1, Vector4f v2, float alpha) 
	{
        double dot = 0, s1, s2, om, sinom;
        //求两个四元数的点积
        for(int i=0; i<4; i++)
        {
        	dot +=  v2.coording[i] * v1.coording[i];
        }        	
        Vector4f v0 = null;
        //若点积值小于0则将 v1置反，同时将点积值置反
        if (dot < 0) 
        {
        	v0 = new Vector4f();
        	for(int i=0; i<4; i++)
        	{
        		v0.coording[i] = - v1.coording[i];
        	}        		
            dot = -dot;
        } 
        else  //否则直接采用v1的值
        {
        	v0 = v1;
        }
       
        //若点积值接近1，则说明两个四元数表示的旋转非常接近
        //直接线性插值即可
        if(dot>0.999999)
        {
        	s1 = 1.0 - alpha;
            s2 = alpha;
        }
        else
        {//否则按照思维空间中的圆弧来计算插值系数
            om = Math.acos(dot);
            sinom = Math.sin(om);
            s1 = Math.sin((1.0 - alpha) * om) / sinom;
            s2 = Math.sin(alpha * om) / sinom;
        } 
        
        //通过插值系数s1、s2计算v1、v2两个四元比例数插值后
        //的新四元数的四个分量
        this.setW((float) (s1 * v0.getW() + s2 * v2.getW()));
        this.setX((float) (s1 * v0.getX() + s2 * v2.getX()));
        this.setY((float) (s1 * v0.getY() + s2 * v2.getY()));
        this.setZ((float) (s1 * v0.getZ() + s2 * v2.getZ()));
    }
	
	public final void setX(float x) 
	{
		this.coording[0] = x;
	}
	
	public final void setY(float y) 
	{
		this.coording[1] = y;
	}
	
	public final void setZ(float z) 
	{
		this.coording[2] = z;
	}
	
	public final void setW(float w) 
	{
		this.coording[3] = w;
	}
	
	public final float getX() 
	{
		return this.coording[0];
	}
	
	public final float getY() 
	{
		return this.coording[1];
	}
	
	public final float getZ() 
	{
		return this.coording[2];
	}
	
	public final float getW() 
	{
		return this.coording[3];
	}
	
}
