package com.amap.map3d.demo.opengl.ms3dmodel;

import android.content.Context;
import android.graphics.PointF;

import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.opengl.ms3dmodel.gl10.MS3DMaterial_GL10;
import com.amap.map3d.demo.opengl.ms3dmodel.gl10.MS3DModel_GL10;
import com.amap.map3d.demo.opengl.ms3dmodel.gl10.TextureManager_GL10;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DGroup;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DHeader;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DJoint;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DTriangle;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DVertex;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.SmallEndianInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zxy on 16/8/23.
 */
public class TigerModel extends MS3DModel_GL10{

    PointF position;
    LatLng latLngPoint=null;

    public void setLatlngPosition(LatLng latLng) {
        this.latLngPoint = latLng;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }


    private TigerModel(Context context) {
        super(context);
    }

    public static MS3DModel_GL10 cloneModel(MS3DModel_GL10 ms3dModel, Context context){
        TigerModel model = null;
        model = new TigerModel(context);
        model.textureManager = ms3dModel.getTextureManager();//纹理管理器
        model.header = ms3dModel.header.clone();	//加载头信息
        model.vertexs = ms3dModel.vertexs.clone();//加载顶点信息
        model.triangles = ms3dModel.triangles.clone();//MS3DTriangle.load(fis);//加载三角形组装索引信息
        model.groups = ms3dModel.groups.clone();//MS3DGroup.load(fis);//加载组信息
        model.materials = ms3dModel.materials.clone();//MS3DMaterial_GL10.load(fis, manager);//加载材质信息
        model.fps = ms3dModel.fps;//fis.readFloat();//加载帧速率信息
        model.current_time = ms3dModel.current_time;//fis.readFloat();//当前时间
        model.frame_count = ms3dModel.frame_count;//fis.readInt();//关键帧数
        model.totalTime = model.frame_count / model.fps;//计算动画总时间
        model.joints = ms3dModel.joints.clone();//MS3DJoint.load(fis);//加载关节信息
        model.initBuffer();//初始化缓冲

        return model;
    }


    public static MS3DModel_GL10 loadTiger(InputStream is, TextureManager_GL10 manager, Context context){
        TigerModel model = null;
        SmallEndianInputStream fis = null;
        try{
            //将输入流封装为SmallEndian格式的输入流
            fis = new SmallEndianInputStream(is);
            model = new TigerModel(context);
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


}
