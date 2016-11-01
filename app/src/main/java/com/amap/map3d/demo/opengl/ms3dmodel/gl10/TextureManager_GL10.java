package com.amap.map3d.demo.opengl.ms3dmodel.gl10;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLUtils;
import android.util.Log;

import com.amap.map3d.demo.opengl.ms3dmodel.util.BitmapReader;

import java.util.LinkedHashMap;
import java.util.Map;

//纹理管理器
public class TextureManager_GL10 {
	Map<String, Integer> textures = new LinkedHashMap<String, Integer>();
	Resources res;
	public TextureManager_GL10(Resources res){
		this.res=res;
	}
	//添加新纹理的方法
	public void addTexture(String texName, String fileName){
		if(!textures.keySet().contains(texName)){
			Bitmap bitmap = null;
			try{
				bitmap = BitmapReader.load(fileName,res);
		        int[] textures = new int[1];
				GLES10.glGenTextures(
						1,          //产生的纹理id的数量
						textures,   //纹理id的数组
						0           //偏移量
				);    
		        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textures[0]);
				GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER,GLES10.GL_NEAREST);
				GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D,GLES10.GL_TEXTURE_MAG_FILTER,GLES10.GL_LINEAR);
				GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S,GLES10.GL_CLAMP_TO_EDGE);
				GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T,GLES10.GL_CLAMP_TO_EDGE);
			        GLUtils.texImage2D
		        (
		        		GLES10.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
		        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
		        		bitmap, 			  //纹理图像
		        		0					  //纹理边框尺寸
		        );
			    
			    bitmap.recycle();				
				this.textures.put(texName, textures[0]);						
			} 
			catch(Exception e) 
			{
				Log.d("test1", e.toString());
				e.printStackTrace();
			}}}
		//获取指定名称的纹理对应的纹理id的方法
	public final int getTexture(String texName){
		Integer tex = this.textures.get(texName); 
		return (tex != null)?tex.intValue():0;
	}
	//绘制前调用使用指定纹理的方法
	public final void fillTexture(String texName){
		int texture = this.getTexture(texName);
//		Log.d("test1", texName +" "+ texture);
		if(texture != 0){
			//启用2D纹理
			GLES10.glEnable(GLES10.GL_TEXTURE_2D);			
			//绑定纹理
	        GLES10.glActiveTexture(GLES10.GL_TEXTURE0);
	        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, texture);
		}}
	//释放指定名称的纹理资源
	public final void dispose(String texName){
		if(textures.keySet().contains(texName)){
			GLES10.glDeleteTextures(1, new int[]{this.textures.get(texName)}, 0);
			System.gc();
		}}}
