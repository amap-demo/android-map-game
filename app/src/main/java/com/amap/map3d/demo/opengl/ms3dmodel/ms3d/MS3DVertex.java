package com.amap.map3d.demo.opengl.ms3dmodel.ms3d;

import com.amap.map3d.demo.opengl.ms3dmodel.util.Vector3f;

import java.io.IOException;

public class MS3DVertex implements Cloneable{//顶点信息类
	private Vector3f initPosition;	//从文件中读取的顶点原始xyz坐标
	private Vector3f currPosition;	//动画中实时变化的顶点xyz坐标
	private byte bone;			//骨骼ID	
	private MS3DVertex() {}
	//读取顶点信息
	public static MS3DVertex[] load(SmallEndianInputStream is) throws IOException{
		int count = is.readUnsignedShort();//读取顶点数量 
		MS3DVertex[] vertexs = new MS3DVertex[count];//创建顶点信息对象数组
		for(int i=0; i<count; i++){//循环读取每个顶点的信息
			MS3DVertex vertex = new MS3DVertex();
			is.readByte();  //标志--暂时无用，读了扔掉
			vertex.initPosition = 			//顶点XYZ坐标
			  new Vector3f(is.readFloat(),is.readFloat(),is.readFloat());
			vertex.bone = is.readByte();//骨骼ID
			is.readByte();  //标志--暂时无用，读了扔掉
			vertexs[i] = vertex;
		}
		return vertexs;
	}
	public final Vector3f getInitPosition(){
		return initPosition;
	}
	public final byte getBone(){
		return bone;
	}
	public final Vector3f getCurrPosition(){
		return currPosition;
	}
	public final void setCurrPosition(Vector3f buffer){
		this.currPosition = buffer;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
