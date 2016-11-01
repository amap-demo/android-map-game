package com.amap.map3d.demo.opengl.ms3dmodel.gl10;

import android.content.Context;
import android.opengl.GLES10;

import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DGroup;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DHeader;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DJoint;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DTriangle;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DVertex;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.SmallEndianInputStream;
import com.amap.map3d.demo.opengl.ms3dmodel.util.MatrixState;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MS3DModel_GL10 {
	public FloatBuffer[] vertexCoordingBuffer;	//顶点坐标数据缓冲
	public FloatBuffer[] texCoordingBuffer;	//纹理坐标数据缓冲
	public TextureManager_GL10 textureManager;		//纹理管理器
	public MS3DHeader header;				//头信息
	public MS3DVertex[] vertexs;			//顶点信息
	public MS3DTriangle[] triangles;		//三角形索引
	public MS3DGroup[] groups;				//组信息
	public MS3DMaterial_GL10[] materials;		//材质信息(目前用到的主要是纹理部分)
	public float fps;						//fps信息
	public float current_time;				//当前时间
	public float totalTime;					//总时间
	public float frame_count;				//关键帧数
	public MS3DJoint[] joints;				//关节信息
//	ShaderProgram mProgram;//自定义渲染管线程序id
	public MS3DModel_GL10(Context context){
		//initShader(context);
		//opengl es1.0不支持shader
	}
    //初始化着色器
    public void initShader(Context context){
//		mProgram = new ShaderProgram(context,
//				R.raw.base_vertex_shader, R.raw.base_fragment_shader);
    }

	boolean isFirst = true;
	//进行动画的方法
	public final void animate(float time){
		if(this.current_time != time){//相同时间不做更新
			this.updateJoint(time);	//更新关节
			this.updateVectexs();	//更新顶点
			this.draw(true);	//执行绘制
		}
		else{
			//执行绘制

			this.draw(false);
		}
}
	public void updateJoint(float time){//更新关节的方法
		this.current_time = time;//更新当前时间
		if(this.current_time > this.totalTime){//时间超过总时间置为零
			this.current_time = 0.0f;
		}			
		int size = this.joints.length;	//获取关节数量
		for(int i=0; i<size; i++){//更新每个关节
			this.joints[i].update(this.current_time);
		}}
	public void draw(boolean isUpdate){//绘制模型
//   	    mProgram.useProgram(); //指定使用某套shader程序
   	    MatrixState.copyMVMatrix();
        //将最终变换矩阵传入shader程序
	//	mProgram.setMatrix(MatrixState.getFinalMatrix());

		//计算顶点



		//启用顶点坐标数组
        int group_size = this.groups.length;         
        MS3DTriangle triangle = null;
        MS3DGroup group = null;
        int[] indexs = null;  
        int[] vertexIndexs = null;	//顶点索引
        FloatBuffer buffer = null;	//buffer缓冲
        MS3DMaterial_GL10 material = null;   //材质
        for(int i=0; i<group_size; i++){
        	group = this.groups[i];	//获取当前组信息对象
        	indexs = group.getIndicies();//获取组内三角形的索引数组
        	int triangleCount  = indexs.length;//获取组内三角形的数量
        	//有材质（这里主要是指需要贴纹理）

			GLES10.glPushMatrix();
			GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        	if(group.getMaterialIndex() > -1){
        		material = this.materials[group.getMaterialIndex()];
        		this.textureManager.fillTexture(material.getName());

				this.texCoordingBuffer[i].position(0);

				GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
				GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT,2 * 4, this.texCoordingBuffer[i]);
        	}
        	if(isUpdate){//更新顶点缓冲
        		buffer = this.vertexCoordingBuffer[i];
        		for(int j=0; j<triangleCount; j++){//对组内的每个三角形循环
        			triangle = this.triangles[indexs[j]];//获取当前要处理的三角形信息对象
        			vertexIndexs = triangle.getIndexs();	//获取三角形中三个顶点的顶点索引
        			//将三角形的三个顶点的数据放入顶点数据缓冲（实际是完成三角形的组装）

        			for(int k=0; k<3; k++){
//						Vector3f vector3f = this.vertexs[vertexIndexs[k]].getCurrPosition();
//						float[] source = new float[4];
//						source[0] = vector3f.getX();
//						source[1] = vector3f.getY();
//						source[2] = vector3f.getZ();
//						source[3] = 1;
//						float[] result = new float[4];
//						Matrix.multiplyMV(result,0,MatrixState.getFinalMatrix(),0,source,0);
//        				buffer.put(result[0]);
//        				buffer.put(result[1]);
//        				buffer.put(result[2]);

						buffer.put(this.vertexs[vertexIndexs[k]].getCurrPosition().getVector3fArray());
        			}}
        		buffer.position(0);
        	}

			GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 3*4, this.vertexCoordingBuffer[i]);


//			mProgram.setPositionAttribute(this.vertexCoordingBuffer[i], 3);

//            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangleCount * 3);
			GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, triangleCount * 3);

			GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
			GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
			GLES10.glPopMatrix();
        }}
	private void updateVectexs(){//动画中更新顶点数据的方法
		int count = this.vertexs.length;//获取顶点数量
		for(int i=0; i<count; i++){	//更新每个顶点
			this.updateVectex(i);
		}}
	private void updateVectex(int index){//更新特定顶点的方法
		//获取当前需要更新的顶点对应的顶点信息对象
		MS3DVertex vertex = this.vertexs[index];
		//是否有关节骨骼ID
		if(vertex.getBone() == -1){//无关节控制
			
			vertex.setCurrPosition(vertex.getInitPosition());
		} 
		else{//有关节控制
			MS3DJoint joint = this.joints[vertex.getBone()];//获取对应的关节
			//根据关节的实时变换情况计算出顶点经关节影响后的位置
			vertex.setCurrPosition(joint.getMatrix().transform(joint.getAbsolute().invTransformAndRotate(vertex.getInitPosition())));
		}}
	//加载模型的方法
	public final static MS3DModel_GL10 load(InputStream is, TextureManager_GL10 manager, Context context){
		MS3DModel_GL10 model = null;
		SmallEndianInputStream fis = null;
		try{
			//将输入流封装为SmallEndian格式的输入流
			fis = new SmallEndianInputStream(is);
			model = new MS3DModel_GL10(context);
			model.textureManager = manager;//纹理管理器
			model.header = MS3DHeader.load(fis);	//加载头信息
			model.vertexs = MS3DVertex.load(fis);//加载顶点信息
			model.triangles = MS3DTriangle.load(fis);//加载三角形组装索引信息
			model.groups = MS3DGroup.load(fis);//加载组信息
			model.materials = MS3DMaterial_GL10.load(fis, manager);//加载材质信息
			model.fps = fis.readFloat();//加载帧速率信息
			model.current_time = fis.readFloat();//当前时间
			model.frame_count = fis.readInt();//关键帧数
			model.totalTime = model.frame_count / model.fps;//计算动画总时间
			model.joints = MS3DJoint.load(fis);//加载关节信息
			model.initBuffer();//初始化缓冲
		} 
		catch (IOException e){
			e.printStackTrace();
		} 
		finally{
			if(fis != null){
				try {
					fis.close();
					} 
				catch (IOException e){
					e.printStackTrace();
				}}}
		System.gc();//申请垃圾回收
		return model;
	}
	protected void initBuffer(){//初始化缓冲
		//将关节更新到起始时间（时间为0的时间）
		this.updateJoint(0.0f);
		this.updateVectexs();//更新顶点坐标	
		int count = this.groups.length;//组数量
		int triangleCount = 0;//每组三角形个数
		MS3DGroup group = null;//临时组信息
		MS3DTriangle triangle = null;//临时三角形信息
		this.texCoordingBuffer = new FloatBuffer[count];//材质坐标缓冲
		this.vertexCoordingBuffer = new FloatBuffer[count];//顶点坐标缓冲
		int[] indexs = null;//三角形索引
		int[] vertexIndexs = null;//顶点索引
		FloatBuffer buffer = null;	//数据缓冲
		for(int i=0; i<count; i++){//对模型中的每个组进行循环处理
			group = this.groups[i];//获取当前要处理的组
			indexs = group.getIndicies();  //获取组内三角形索引数组
			triangleCount = indexs.length;//获取组内三角形的数量
			//根据组内三角形的数量开辟合适大小的纹理坐标缓冲
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCount*6*4);
			byteBuffer.order(ByteOrder.nativeOrder());
			this.texCoordingBuffer[i] = byteBuffer.asFloatBuffer();
			//根据组内三角形的数量开辟合适大小的顶点坐标缓冲
			byteBuffer = ByteBuffer.allocateDirect(triangleCount*9*4);
			byteBuffer.order(ByteOrder.nativeOrder());
			this.vertexCoordingBuffer[i] = byteBuffer.asFloatBuffer();
			//循环对组内的每个三角形进行处理
			for(int j=0; j<triangleCount; j++){
				triangle = this.triangles[indexs[j]];//获取当前要处理的三角形
				vertexIndexs = triangle.getIndexs();//获取三角形中各个顶点的索引
				for(int k=0; k<3; k++){//对三角形中的三个顶点进行循环处理
					//获取当前组的纹理坐标数据缓冲
					buffer = this.texCoordingBuffer[i];
					//将当前遍历到的顶点的纹理ST坐标送入缓冲
					buffer.put(triangle.getS().getVector3fArray()[k]);
					buffer.put(triangle.getT().getVector3fArray()[k]);
					//获取当前组的顶点坐标数据缓冲
					buffer = this.vertexCoordingBuffer[i];
					//将当前遍历到的顶点的坐标送入缓冲
					buffer.put(this.vertexs[vertexIndexs[k]].getCurrPosition().getVector3fArray());
				}}
			//设置当前组的纹理坐标缓冲起始位置为0
			this.texCoordingBuffer[i].position(0);
			//设置当前组的顶点坐标缓冲起始位置为0
			this.vertexCoordingBuffer[i].position(0);
		}}
	public final float getTotalTime(){
		return totalTime;    
	}


	public TextureManager_GL10 getTextureManager() {
		return textureManager;
	}
}
