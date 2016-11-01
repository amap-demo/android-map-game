package com.amap.map3d.demo.opengl.particle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES10;
import android.opengl.GLUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ParticleMapRender implements CustomRenderer {


    private boolean isNeedCalPoint = true;
    private float[] translate_vector = new float[4];
    public static float SCALE = 0.005F;// 缩放暂时使用这个

    private LatLng center = new LatLng(39.90403, 116.407525);// 北京市经纬度

    private ParticleSystem obj ;

    private AMap aMap;

    private Context context;

    public ParticleMapRender(AMap aMap, Context context) {
        this.aMap = aMap;
        this.context = context;

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 14));
    }

    float offset = 0.001f;

    long lastTime = 0L;


    @Override
    public void onDrawFrame(GL10 gl) {
        if(obj != null) {
            if(isNeedCalPoint) {
                obj.updateReference(translate_vector, SCALE);
                isNeedCalPoint = false;
            }
            obj.updateParticle();
            obj.draw();
        }
//        long curTime = System.currentTimeMillis();
//        if(curTime - lastTime > 16) {
//
//
//            lastTime = curTime;
//
//            //来回移动
////            offset = -offset;
//            center = new LatLng(center.latitude + offset,center.longitude);
//
//            //重新计算偏移位置
//            calScaleAndTranslate();
//
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(center));
//        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        obj = new ParticleSystem();
        //绑定纹理id
        obj.setTextureId(loadGLTexture(context, R.drawable.particle_trans_map));
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


    /**
     * Load the textures
     *
     * @param context - The ParticleActivity context
     * @param resourceID - The texture from the resource directory
     */
    public int loadGLTexture(Context context, int resourceID) {
        int[] textures = new int[1];

        if (context != null ) {
            InputStream is = context.getResources().openRawResource(resourceID);
            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream(is);

            } finally {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                }
            }

            //生成id，n为参数个数，textures生成之后存放的位置
            GLES10.glGenTextures(1, textures, 0);
            //加载纹理
            GLES10.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

            GLES10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            GLES10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            GLES10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            GLES10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

            //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();
        }

        return textures[0];
    }




}
