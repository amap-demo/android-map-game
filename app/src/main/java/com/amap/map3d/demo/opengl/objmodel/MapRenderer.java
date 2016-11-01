package com.amap.map3d.demo.opengl.objmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES10;
import android.opengl.GLUtils;
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
        readFromRaw(R.raw.jeep);
        model = new Model(vertext_list,textrue_list);
        int tid = loadGLTexture(context, R.drawable.jeep_texture6);
        model.setTextureId(tid);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 注2：绘制各种图形的opengl代码

        model.draw();

    }

    @Override
    public void OnMapReferencechanged() {
        //注3：回调这个时，坐标系发生改变，需要重新计算缩放比例
        calScaleAndTranslate();
        model.update(translate_vector,SCALE);

    }

    private void calScaleAndTranslate() {
        // 坐标会变化，重新计算计算偏移，供参考，可以自行定义
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