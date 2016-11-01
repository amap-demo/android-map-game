package com.amap.map3d.demo.opengl.cube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.model.LatLng;

public class CubeMapRender implements CustomRenderer {

    private boolean isNeedCalPoint = true;
    private float[] translate_vector = new float[4];
    public static float SCALE = 0.005F;// 缩放暂时使用这个

    private LatLng center = new LatLng(39.90403, 116.407525);// 北京市经纬度

    private Cube cube ;

    private AMap aMap;

    public CubeMapRender(AMap aMap) {
        this.aMap = aMap;

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
    }

    float offset = 0.0005f;

    long lastTime = 0L;

    @Override
    public void onDrawFrame(GL10 gl) {
        if(cube != null) {
            if(isNeedCalPoint) {
                cube.update(translate_vector, SCALE);
                isNeedCalPoint = false;
            }
            cube.draw();
        }
        long curTime = System.currentTimeMillis();
        if(curTime - lastTime > 200) {
            lastTime = curTime;

            //来回移动
            offset = -offset;
            center = new LatLng(center.latitude + offset,center.longitude + offset);
            //重新计算偏移位置
            calScaleAndTranslate();
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //创建cube
        cube = new Cube(2,2,2);
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
        SCALE = (float) Math.sqrt((_x * _x + _y * _y));


        isNeedCalPoint = true;
    }
}
