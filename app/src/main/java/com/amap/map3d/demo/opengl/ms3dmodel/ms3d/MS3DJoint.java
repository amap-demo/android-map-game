package com.amap.map3d.demo.opengl.ms3dmodel.ms3d;

import com.amap.map3d.demo.opengl.ms3dmodel.util.Matrix4f;
import com.amap.map3d.demo.opengl.ms3dmodel.util.Vector3f;
import com.amap.map3d.demo.opengl.ms3dmodel.util.Vector4f;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MS3DJoint {// 关节信息
	private String name; // 关节名称
	private String parentName; // 父关节名称
	private MS3DJoint parent; // 父关节
	private Vector3f rotate; // 初始旋转值
	private Vector3f position; // 初始位置
	private MS3DKeyFrameRotate[] rotates; // 关键帧旋转值
	private MS3DKeyFramePosition[] positions; // 关键帧位置坐标
	private Matrix4f relative; // 相对矩阵（子关节在父关节坐标系中的变换矩阵）
	private Matrix4f absolute; // 初始绝对矩阵（子关节在世界坐标系中的初始变换矩阵）
	private Matrix4f matrix; // 变换矩阵（当前绝对矩阵）
	private MS3DJoint() {
	}
	// 加载关节信息
	public final static MS3DJoint[] load(SmallEndianInputStream is)throws IOException {
		int count = is.readUnsignedShort();// 获取关节数量
		MS3DJoint[] joints = new MS3DJoint[count];// 创建关节信息对象数组
		// 创建关节信息对象与名称的map
		Map<String, MS3DJoint> map = new LinkedHashMap<String, MS3DJoint>();
		for (int i = 0; i < count; i++) {// 循环加载每个关节的信息
			MS3DJoint joint = new MS3DJoint();
			is.readByte();// 标志 暂时无用，读了扔掉
			joint.name = is.readString(32);// 读取关节名称
			joint.parentName = is.readString(32);// 读取父关节名称
			// 加载关节的旋转数据
			joint.rotate = new Vector3f(is.readFloat(), is.readFloat(),is.readFloat());
			// 加载关节的位置数据
			joint.position = new Vector3f(is.readFloat(), is.readFloat(),is.readFloat());
			// 读取关节旋转的关键帧数量
			int numKeyFramesRot = is.readUnsignedShort();
			// 读取关节平移的关键帧数量
			int numKeyFramesPos = is.readUnsignedShort();
			// 若关节旋转的关键帧数量不为0，则加载关节旋转的关键帧的值
			if (numKeyFramesRot > 0) {
				joint.rotates = MS3DKeyFrameRotate.load(is, numKeyFramesRot);
			}
			// 若关节平移的关键帧数量不为0，则加载关节平移的关键帧的值
			if (numKeyFramesPos > 0) {
				joint.positions = MS3DKeyFramePosition.load(is, numKeyFramesPos);
			}
			joints[i] = joint;
			map.put(joint.name, joint);// 将关节信息对象存储进map以备查找
			joint.parent = map.get(joint.parentName); // 获得此关节的父关节
			joint.relative = new Matrix4f();// 设置相对矩阵
			joint.relative.loadIdentity();
			// 设置旋转
			joint.relative.genRotationFromEulerAngle(joint.rotate);
			joint.relative.setTranslation(joint.position); // 设置平移
			joint.absolute = new Matrix4f();// 设置绝对矩阵
			joint.absolute.loadIdentity();
			if (joint.parent != null) {// 是否有父关节
				// 有父关节的话绝对矩阵等于父关节的绝对矩阵乘以子关节的相对矩阵
				joint.absolute.mul(joint.parent.absolute, joint.relative);
			} else {
				// 无父关节的话相对矩阵即为绝对矩阵
				joint.absolute.copyFrom(joint.relative);
			}}
		map.clear();// 清除map
		map = null;// 清除map
		return joints;
	}
	public final void update(float time) {// 更新此关节
		// 无旋转无平移的关节其当前绝对矩阵等于初始绝对矩阵
		if (this.rotates == null && this.positions == null) {
			if (this.matrix == null) {
				this.matrix = new Matrix4f();
			}
			this.matrix.copyFrom(this.absolute);
			return;
		}
		Matrix4f matrix = this.rotate(time);// 先旋转
		matrix.setTranslation(this.position(time));// 再平移
		matrix.mul(this.relative, matrix);// 与自身相对矩阵相乘
		if (this.parent != null) {// 是否有父关节
			this.matrix = matrix.mul(this.parent.matrix, matrix);// 有父关节
		} else {
			this.matrix = matrix;// 无父关节
		}}
	private Matrix4f rotate(float time) {// 根据当前播放时间进行旋转插值计算
		Matrix4f m = new Matrix4f();
		int index = 0;
		int size = this.rotates.length;
		// 根据时间确定当前帧应该由哪一个起始关键帧往后计算
		while (index < size && this.rotates[index].getTime() < time) {
			index++;
		}
		if (index == 0) {
			m.genRotateFromQuaternion(this.rotates[0].getRotate());
		} 
		else if (index == size) {
			m.genRotateFromQuaternion(this.rotates[size - 1].getRotate());
		}
		else {
			MS3DKeyFrameRotate left = this.rotates[index - 1];// 上一关键帧的旋转
			MS3DKeyFrameRotate right = this.rotates[index];// 此关键帧的旋转
			Vector4f v = new Vector4f();// 插值产生当前帧的旋转
			v.interpolate(left.getRotate(), right.getRotate(),(time - left.getTime())/(right.getTime() - left.getTime()));
			// 将四元数形式的旋转变为矩阵形式
			m.genRotateFromQuaternion(v);
		}
		return m;
	}
	private Vector3f position(float time) {// 根据当前播放时间进行平移插值计算
		int index = 0;
		int size = this.positions.length;
		// 根据时间确定当前帧应该由哪一个起始关键帧往后计算
		while (index < size && this.positions[index].getTime() < time) {
			index++;
		}
		if (index == 0) {
			return this.positions[0].getPosition();
		} 
		else if (index == size) {
			return this.positions[size - 1].getPosition();
		} 
		else {
			// 上一关键帧的平移
			MS3DKeyFramePosition left = this.positions[index - 1];
			// 此关键帧的平移
			MS3DKeyFramePosition right = this.positions[index];
			// 插值计算出当前关键帧的平移
			Vector3f v = new Vector3f();
			v.interpolate(left.getPosition(), right.getPosition(),
					(time - left.getTime())
							/ (right.getTime() - left.getTime()));
			return v;
		}}
	public final Matrix4f getMatrix() {
		return matrix;
	}
	public final Matrix4f getAbsolute() {
		return absolute;
	}}
