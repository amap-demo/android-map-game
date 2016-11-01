package com.amap.map3d.demo.opengl.ms3dmodel.ms3d;

import com.amap.map3d.demo.opengl.ms3dmodel.util.Vector3f;

import java.io.IOException;

//关节平移关键帧信息
public class MS3DKeyFramePosition {
	private float time; // 时间(单位为秒)
	private Vector3f position; // 位置坐标
	private MS3DKeyFramePosition() {
	}
	// 加载关节平移的关键帧信息
	public final static MS3DKeyFramePosition[] load(SmallEndianInputStream is, int num) throws IOException {
		// 创建关节平移关键帧信息对象数组
		MS3DKeyFramePosition[] positions = new MS3DKeyFramePosition[num];
		// 循环加载每个平移关键帧的信息
		for (int i = 0; i < num; i++) {
			MS3DKeyFramePosition position = new MS3DKeyFramePosition();
			position.time = is.readFloat();// 读取关键帧时间
			// 读取关键帧位置信息
			position.position = new Vector3f(is.readFloat(), is.readFloat(),is.readFloat());
			positions[i] = position;
		}
		return positions;
	}
	public final float getTime() {
		return time;
	}
	public final Vector3f getPosition() {
		return position;
	}}
