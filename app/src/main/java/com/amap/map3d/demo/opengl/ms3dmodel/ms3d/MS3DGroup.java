package com.amap.map3d.demo.opengl.ms3dmodel.ms3d;
import java.io.IOException;

//组信息
public class MS3DGroup{	
	private int[] indicies;		//组内的三角形的对应索引	
	private byte materialIndex;	//材质索引
	private MS3DGroup(){} 
	//加载组信息的方法
	public final static MS3DGroup[] load(SmallEndianInputStream is) throws IOException{
		int count = is.readUnsignedShort();//读取组数量
		//创建组信息对象数组
		MS3DGroup[] groups = new MS3DGroup[count];
		for(int i=0; i<count; i++){//循环加载每个组的信息
			MS3DGroup group = new MS3DGroup();
			is.readByte();  //标志--暂时无用，读了扔掉
			is.readString(32);//读取组名称--暂时无用，读了扔掉
			int indexCount = is.readUnsignedShort();//读取组内三角形数量
			group.indicies = new int[indexCount];//创建组内三角形索引数组
			for(int j=0; j<indexCount; j++){ //加载组内各个三角形的索引
				group.indicies[j] = is.readUnsignedShort();
			}
			group.materialIndex = is.readByte();//读取材质索引
			groups[i] = group;
		}
		return groups;
	}
	public final int[] getIndicies(){
		return indicies;//组内的三角形的对应索引
	}
	public final byte getMaterialIndex(){
		return materialIndex;//材质索引
	}}
