package com.amap.map3d.demo.opengl.ms3dmodel.ms3d;

import com.amap.map3d.demo.opengl.ms3dmodel.util.TextureManager;

import java.io.IOException;

//材质信息
public class MS3DMaterial{	
	String name;		//材质名称	
	float[] ambient_color;	//环境光	
	float[] diffuse_color;	//散射光	
	float[] specular_color;	//镜面光	
	float[] emissive_color;	//自发光	
	float shininess;		//粗糙度 0-128	
	float transparency;		//透明度 0-1	
	String textureName;		//材质文件名称	
	private MS3DMaterial() {}
	//加载材质信息的方法
	public final static MS3DMaterial[] load(SmallEndianInputStream is, TextureManager manager) throws IOException{
		int count = is.readUnsignedShort();//读取材质数量
		//创建材质信息对象数组
		MS3DMaterial[] mals = new MS3DMaterial[count];
		for(int i=0; i<count; i++){//循环加载每个材质的信息
			MS3DMaterial mal = new MS3DMaterial();
			mal.name = is.readString(32);//读取材质的名称
			mal.ambient_color = new float[4];//读取环境光信息
			for(int j=0; j<4; j++){
				mal.ambient_color[j] = is.readFloat();
			}
			mal.diffuse_color = new float[4];//读取散射光信息
			for(int j=0; j<4; j++){
				mal.diffuse_color[j] = is.readFloat();
			}
			mal.specular_color = new float[4];//读取镜面光信息
			for(int j=0; j<4; j++){
				mal.specular_color[j] = is.readFloat();
			}
			mal.emissive_color = new float[4];//读取自发光信息
			for(int j=0; j<4; j++){
				mal.emissive_color[j] = is.readFloat();
			}
			mal.shininess = is.readFloat();//读取粗糙度信息
			mal.transparency = is.readFloat();//读取透明度信息
			is.readByte();//mode 暂时无用，读了扔掉
			//读取纹理图片名称
			mal.textureName = format(is.readString(128));
			is.readString(128);//透明材质 暂时无用，读了扔掉
			mals[i] = mal;
			//添加纹理（也就是加载纹理图）
			manager.addTexture(mal.name, mal.textureName);
		}
		return mals;
	}
	//从文件路径中摘取出纹理图的文件名，如“xx.jpg”
	private final static String format(String path){
		int offset = path.lastIndexOf("\\");
		if(offset > -1) {
			return path.substring(offset + 1);
		}
		return path;
	}
	public final String getName(){
		return name;
	}}
