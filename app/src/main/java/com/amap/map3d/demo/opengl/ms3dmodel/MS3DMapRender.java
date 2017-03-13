package com.amap.map3d.demo.opengl.ms3dmodel;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.opengl.ms3dmodel.ms3d.MS3DModel;
import com.amap.map3d.demo.opengl.ms3dmodel.util.MatrixState;
import com.amap.map3d.demo.opengl.ms3dmodel.util.TextureManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zxy94400 on 2016/3/19.
 */
public class MS3DMapRender implements CustomRenderer {

    private boolean isNeedCalPoint = true;
    private float[] translate_vector = new float[4];
    public static float SCALE = 0.005F;// 缩放暂时使用这个

    private LatLng center = new LatLng(39.90403, 116.407525);// 北京市经纬度

    TextureManager manager;	//纹理管理器
    MS3DModel ms3d;			//ms3d模型
    float time = 12.3f;			//当前时间（用于动画播放）

    List<MS3DModel> ms3ds = new ArrayList<MS3DModel>();

    private AMap aMap;

    private Context mContext;

    public MS3DMapRender(AMap aMap, Context context) {
        this.aMap = aMap;
        this.mContext = context;

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,10));

        aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                randomPostion();
            }
        });
    }

    public void randomPostion() {
        for( MS3DModel ms3DModel : ms3ds) {

            if(ms3DModel instanceof TigerModelGL20) {
                LatLng latLng = new LatLng(center.latitude + (random.nextInt(4) - 2) + random.nextDouble(),
                        center.longitude + random.nextInt(4) - 2 + random.nextDouble());
                PointF pointF1 = aMap.getProjection().toOpenGLLocation(latLng);
//                PointF pointF1 = new PointF(pointF.x + i * 2, pointF.y);
                ((TigerModelGL20) ms3DModel).setPosition(pointF1);
                ((TigerModelGL20) ms3DModel).setLatlngPosition(latLng);
            }
        }
    }


    float offset = 0.001f;

    long lastTime = 0L;

    float[] mvp = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {


        if(ms3d != null) {
            MatrixState.pushMatrix();

            for(MS3DModel ms3dModel : ms3ds) {
                MatrixState.pushMatrix();
                if(ms3dModel instanceof TigerModelGL20) {
//                    PointF pointF = ((TigerModelGL20) ms3dModel).position;

                    PointF pointF = aMap.getProjection().toOpenGLLocation(((TigerModelGL20) ms3dModel).latLngPoint);


                    Matrix.setIdentityM(mvp, 0);

                    Matrix.multiplyMM(mvp,0, aMap.getProjectionMatrix(),0,aMap.getViewMatrix(),0);
                    Matrix.translateM(mvp, 0 , pointF.x , pointF.y  , 0);

                    int scale = 100;
                    Matrix.scaleM(mvp, 0 , scale, scale, scale);

                    Matrix.rotateM(mvp, 0 ,90,1,0,0);
                    Matrix.rotateM(mvp, 0 ,-90,0,1,0);


                    MatrixState.setMatrix(mvp);

                    ms3dModel.animate(time);
                    time += 0.015f;
                    //若当前播放时间大于总的动画时间则实际播放时间等于当前播放时间减去总的动画时间
                    if (time > ms3dModel.getTotalTime()) {
                        time = 12.3f + time - ms3dModel.getTotalTime();
                    }

//                    //移动距离
//                    LatLng last = ((TigerModelGL20) ms3dModel).latLngPoint;
//                    float offset = 0.00001f;
////                    if(Math.abs(last.latitude - center.latitude) < offset &&
////                            Math.abs(last.longitude - center.longitude) < offset) {
////                        Log.i("zxy","到达终点" );
////                    } else {
//
//                        LatLng latLng = new LatLng(last.latitude + Math.cos(bearing) *offset,
//                                last.longitude + Math.sin(bearing) * offset);
//                        ((TigerModelGL20) ms3dModel).setLatlngPosition(latLng);
//                        //重新计算偏移位置
//                        calScaleAndTranslate();
//                    }




                }
                MatrixState.popMatrix();
            }
            MatrixState.popMatrix();
        }


//
//
//        long curTime = System.currentTimeMillis();
//        if(curTime - lastTime > 500) {
//            lastTime = curTime;
//
//            //来回移动
//            offset = -offset;
//            center = new LatLng(center.latitude + offset,center.longitude + offset);
//            //重新计算偏移位置
//            calScaleAndTranslate();
//        }



    }

    private float getBearing(float x1, float y1, float x2, float y2) {
        float bearing = (float) (Math.atan2(y2 - y1, x2 - x1) / Math.PI * 180);
        return bearing;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        MatrixState.setInitStack();

        manager = new TextureManager(mContext.getResources());
        //获取ms3d文件的输入流
        InputStream in = null;
        try{
            in = mContext.getResources().getAssets().open("tiger.ms3d");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //从输入流加载模型
        ms3d = MS3DModel.load(in,manager,mContext);

        for(int i =0; i < 1; i++) {
            MS3DModel ms3DModel = TigerModelGL20.cloneModel(ms3d,mContext);

            if(ms3DModel instanceof TigerModelGL20) {
//                LatLng latLng = new LatLng(center.latitude + (random.nextInt(2) - 1) + random.nextDouble(),
//                        center.longitude + random.nextInt(2) - 1 + random.nextDouble());
                LatLng latLng = new LatLng(center.latitude + 0.01f,center.longitude + 0.01f);

                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                PointF pointF1 = aMap.getProjection().toOpenGLLocation(latLng);
//                PointF pointF1 = new PointF(pointF.x + i * 2, pointF.y);
                ((TigerModelGL20) ms3DModel).setPosition(pointF1);
                ((TigerModelGL20) ms3DModel).setLatlngPosition(latLng);
            }

            ms3ds.add(ms3DModel);
        }
    }


    @Override
    public void OnMapReferencechanged() {
        calScaleAndTranslate();

    }

    private void calScaleAndTranslate() {
        // 坐标会变化，重新计算计算偏移
        PointF pointF = aMap.getProjection().toOpenGLLocation(center);

        translate_vector[0] = pointF.x;
        translate_vector[1] = pointF.y;
        translate_vector[2] = 0;

        //重新计算缩放比例
        LatLng latLng2 = new LatLng(center.latitude + 0.001, center.longitude + 0.001);
        PointF pointF2 = aMap.getProjection().toOpenGLLocation(latLng2);
        double _x = Math.abs((pointF.x - pointF2.x));
        double _y = Math.abs((pointF.y - pointF2.y));
        SCALE = (float) Math.sqrt((_x * _x + _y * _y)) * 0.008f;


        isNeedCalPoint = true;


        for( MS3DModel ms3DModel : ms3ds) {

            if(ms3DModel instanceof TigerModelGL20) {
                PointF pointF1 = aMap.getProjection().toOpenGLLocation(((TigerModelGL20) ms3DModel).latLngPoint);
//                PointF pointF1 = new PointF(pointF.x + i * 2, pointF.y);
                ((TigerModelGL20) ms3DModel).setPosition(pointF1);
            }
        }
    }

    Random random = new Random();
}
