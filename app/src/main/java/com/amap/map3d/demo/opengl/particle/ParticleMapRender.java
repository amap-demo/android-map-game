package com.amap.map3d.demo.opengl.particle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

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

    float[] mvp = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        if(obj != null) {

            if(ParticleSystem.shader == null){
                ParticleSystem.initShader();
            }


            Matrix.setIdentityM(mvp, 0);

            //偏移
            PointF pointF = aMap.getProjection().toOpenGLLocation(center);

            Matrix.multiplyMM(mvp,0, aMap.getProjectionMatrix(),0,aMap.getViewMatrix(),0);

            Matrix.translateM(mvp, 0 , pointF.x , pointF.y  , 0);
            int scale = 10000;
            Matrix.scaleM(mvp, 0 , scale, scale, scale);


            obj.updateParticle();
            obj.draw(mvp);
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
            GLES20.glGenTextures(1, textures, 0);
            //加载纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();
        }

        return textures[0];
    }




}
