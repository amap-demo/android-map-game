package com.amap.map3d.demo.opengl.ms3dmodel.ms3d;

import com.amap.map3d.demo.opengl.ms3dmodel.util.Vector3f;

import java.io.IOException;

//此类对象用来封装三角形组装的索引信息
public class MS3DTriangle{
	private int[] indexs;		 //组装索引值	
	private Vector3f[] normals;	 //3个顶点法线向量
	private Vector3f s;			 //三个顶点的纹理S坐标
	private Vector3f t;			 //三个顶点的纹理T坐标
	private byte smoothingGroup; //平滑组
	private byte groupIndex;	 //组索引
	private MS3DTriangle() {}
	//加载三角形组装信息对象数组的方法
	public static MS3DTriangle[] load(SmallEndianInputStream is) throws IOException{
		int count = is.readUnsignedShort();//读取三角形数量
	    //创建三角形组装信息对象数组
		MS3DTriangle[] triangles = new MS3DTriangle[count];
		for(int i=0; i<count; i++){//循环加载每一个三角形的组装索引信息
			MS3DTriangle triangle= new MS3DTriangle();
			is.readUnsignedShort();//标志-暂时无用，读了扔掉
			triangle.indexs = new int[]{//加载索引
				is.readUnsignedShort(),
				is.readUnsignedShort(),
				is.readUnsignedShort()
			};
			triangle.normals = new Vector3f[3];//加载三个顶点的法向量
			for(int j=0; j<3; j++){
				triangle.normals[j] = new Vector3f(
					is.readFloat(),
					is.readFloat(),
					is.readFloat()
				);}
			triangle.s = new Vector3f(//加载三个顶点的纹理S坐标
					is.readFloat(),
					is.readFloat(),
					is.readFloat()
			); 
			triangle.t = new Vector3f(//加载三个顶点的纹理T坐标
					is.readFloat(),
					is.readFloat(),
					is.readFloat()
			);
			triangle.smoothingGroup = is.readByte();//加载平滑组信息
			triangle.groupIndex = is.readByte();//加载组信息
			triangles[i] = triangle;
		}
		return triangles;
	}
	public final int[] getIndexs(){
		return indexs;
	}
	public final Vector3f[] getNormals(){
		return normals;
	}
	public final Vector3f getS(){
		return s;
	}
	public final Vector3f getT(){
		return t;
	}
	public final byte getSmoothingGroup(){
		return smoothingGroup;
	}
	public final byte getGroupIndex(){
		return groupIndex;
	}}
