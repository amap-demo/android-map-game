package com.amap.map3d.demo.opengl.ms3dmodel.ms3d;

import com.amap.map3d.demo.opengl.ms3dmodel.util.Vector4f;

import java.io.IOException;

public class MS3DKeyFrameRotate{//关节旋转关键帧信息
	private float time;			//时间(单位为秒)		
	private Vector4f rotate;	//旋转向量
	private MS3DKeyFrameRotate() {}
	//加载关节旋转的关键帧信息
	public final static MS3DKeyFrameRotate[] load(SmallEndianInputStream is, int num) throws IOException{
		//创建关节旋转关键帧信息对象的数组
		MS3DKeyFrameRotate[] rotates = new MS3DKeyFrameRotate[num];
		for(int i=0; i<num; i++){//循环加载所有的旋转关键帧信息
			MS3DKeyFrameRotate rotateKF = new MS3DKeyFrameRotate();
			rotateKF.time = is.readFloat();//读取关键帧时间
			rotateKF.rotate = new Vector4f();//读取关键帧旋转数据
			//将读取到的欧拉角形式的旋转数据转化为四元数形式（这是为了便于在关键帧之间进行插值计算）
			rotateKF.rotate.setFromEulerAngleToQuaternion(is.readFloat(),is.readFloat(),is.readFloat());
			rotates[i] = rotateKF;
		}
		return rotates;//返回旋转向量
	}
	public final float getTime(){
		return time;
	}
	public final Vector4f getRotate(){
		return rotate;
	}}
