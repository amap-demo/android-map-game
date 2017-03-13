package com.amap.map3d.demo.opengl.objmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MapRenderer implements CustomRenderer {

    Model model = null;


    //平移位置
    private float[] translate_vector = new float[4];
    //缩放比例
    public float SCALE = 0.005F;
    private LatLng center = new LatLng(39.90403, 116.407525);// 北京市经纬度
    private AMap aMap;
    private Context context;

    public MapRenderer(AMap aMap,Context context) {
        this.aMap = aMap;
        this.context = context;
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));



    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Model.initShader();
        readFromRaw(R.raw.jeep);
        model = new Model(vertext_list,textrue_list);
        int tid = loadGLTexture(context, R.drawable.jeep_texture6);
        model.setTextureId(tid);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    float[] mvp = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {

        // 注2：绘制各种图形的opengl代码

        if(model != null) {
            Matrix.setIdentityM(mvp, 0);

            //偏移
            PointF pointF = aMap.getProjection().toOpenGLLocation(center);

            Matrix.multiplyMM(mvp,0, aMap.getProjectionMatrix(),0,aMap.getViewMatrix(),0);

            Matrix.translateM(mvp, 0 , pointF.x , pointF.y  , 0);
            int scale = 10000;
            Matrix.scaleM(mvp, 0 , scale, scale, scale);

            model.draw(mvp);
        }


    }

    @Override
    public void OnMapReferencechanged() {

    }



    List<Float> vertext_list = new ArrayList<Float>();
    List<Float> textrue_list = new ArrayList<Float>();


    /**
     * Load the textures
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


    public void readFromRaw(int id) {

        vertext_list.clear();
        textrue_list.clear();
        try {
            InputStream inputStream = context.getResources().openRawResource(id);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while(true) {
                String line = bufferedReader.readLine();
                if(line == null) {
                    break;
                }
                if(line.startsWith("vn")) {
                    // 顶点法线

                } else if(line.startsWith("vt")) {
                    //texture 纹理坐标
                    //按照空格分割
                    String[] strs = line.split(" ");
                    for(int i = 1; i < strs.length; i++) {
                        String string = strs[i];
                        if(!TextUtils.isEmpty(string)) {
                            textrue_list.add(Float.parseFloat(string));
                        }
                    }

                } else if(line.startsWith("v")) {
                    //vertex 顶点坐标

                    //按照空格分割
                    String[] strs = line.split(" ");
                    for(int i = 1; i < strs.length; i++) {
                        String string = strs[i];
                        if(!TextUtils.isEmpty(string)) {
                            vertext_list.add(Float.parseFloat(string));
                        }
                    }

                } else if(line.startsWith("f")) {
                    //面

                }


            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



}