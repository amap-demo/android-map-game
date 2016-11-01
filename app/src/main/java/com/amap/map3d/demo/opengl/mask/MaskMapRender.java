package com.amap.map3d.demo.opengl.mask;

import android.graphics.PointF;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.model.LatLng;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MaskMapRender implements CustomRenderer {

    private boolean isNeedCalPoint = true;
    private float[] translate_vector = new float[4];
    public static float SCALE = 0.005F;// 缩放暂时使用这个

    private LatLng center = new LatLng(39.90403, 116.407525);// 北京市经纬度

    float[] color = {0,0,0,0.5f};

    public void setRGBA(float r,float g,float b,float a) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    public void setRGBA(int r,int g,int b,int a) {
        color[0] = r / 255.0f;
        color[1] = g / 255.0f;
        color[2] = b / 255.0f;
        color[3] = a / 255.0f;
    }

    public void setRGB(int r,int g,int b) {
        setRGBA(r / 255.0f,g / 255.0f ,b / 255.0f ,color[3]);
    }

    private AMap aMap;

    public MaskMapRender(AMap aMap) {
        this.aMap = aMap;

//        aMap.addMarker(new MarkerOptions().position(center));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

//        aMap.addPolyline(new PolylineOptions().add(center, new LatLng(39.983456, 116.3154950))
//                .width(10).color(Color.WHITE).zIndex(10));

//        aMap.showMapText(true);
//        aMap.showBuildings(true);


        //index
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(indices.length * 2);
        byteBuffer2.order(ByteOrder.nativeOrder());
        mIndexBuffer = byteBuffer2.asShortBuffer();
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);


        //顶点坐标
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }


    private int width;
    private int height;

    public FloatBuffer mVertexBuffer;
    public FloatBuffer mcolorBuffer;
    public ShortBuffer mIndexBuffer;

    float vertices[] = {
            -1, -1, 1.0f,
            1, -1, 1.0f,
            -1, 1, 1.0f,
            1, 1, 1.0f,
    };

    short indices[] = {
            0, 1, 3,
            0, 3, 2
    };

    @Override
    public void onDrawFrame(GL10 gl) {


        //1.直接绘制四边形，然后缩放到和整个地图一样大，仰角在45°是 天空会显示出来
        gl.glPushMatrix();



        //平移到地图指定位置
//        gl.glTranslatef(translate_vector[0], translate_vector[1], translate_vector[2]);
        //缩放物体大小适应地图
        gl.glScalef(width / 2.0f, height / 2.0f, 0);

        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);

//        gl.glEnable(GL10.GL_DEPTH_TEST);

        gl.glDisable(GL10.GL_TEXTURE_2D);
        //启用混合模式
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_DST_COLOR);//高亮颜色
//        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

//        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_DST_COLOR);
//        gl.glColor4f(0f, 0f, 0.5f, 0.5f);
//        gl.glColor4f(0.2f, 0.2f, 0.2f, 0.5f);
        gl.glColor4f(color[0],color[1],color[2],color[3]);


        gl.glVertexPointer(3, gl.GL_FLOAT, 0, mVertexBuffer);


        gl.glDrawElements(gl.GL_TRIANGLES, indices.length, gl.GL_UNSIGNED_SHORT, mIndexBuffer);

        gl.glDisableClientState(gl.GL_VERTEX_ARRAY);

        gl.glDisable(GL10.GL_BLEND);

        gl.glPopMatrix();
        gl.glFlush();

        /*//2.绘制四边形，使用正投影,铺满整个窗口
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glViewport(0, 0, width, height);k
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-0, width, -0, height, 1, -1);
//
        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);

//        // 启用混合模式
//        gl.glEnable(GL10.GL_BLEND);
//        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//        // 设置透明显示
//        gl.glEnable(GL10.GL_ALPHA_TEST);
//        gl.glAlphaFunc(GL10.GL_GREATER, 0.2f);

        gl.glVertexPointer(3, gl.GL_FLOAT, 0, mVertexBuffer);

        gl.glColor4f(1, 0, 0, 0.2f);

        gl.glDrawElements(gl.GL_TRIANGLES, indices.length, gl.GL_UNSIGNED_SHORT, mIndexBuffer);

        gl.glDisableClientState(gl.GL_VERTEX_ARRAY);


        gl.glLoadIdentity();
        gl.glPopMatrix();
        gl.glFlush();*/
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;

        /*//绘制四边形，使用正投影,铺满整个窗口 设置vertext
        vertices[0] = -width;
        vertices[1] = -height;
        vertices[2] = 0;

        vertices[3] = -width;
        vertices[4] = height;
        vertices[5] = 0;

        vertices[6] = width;
        vertices[7] = -height;
        vertices[8] = 0;

        vertices[9] = width;
        vertices[10] = height;
        vertices[11] = 0;

        //顶点坐标
        if(mVertexBuffer == null) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            mVertexBuffer = byteBuffer.asFloatBuffer();
        }
        mVertexBuffer.clear();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);*/


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

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
