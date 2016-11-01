package com.amap.map3d.demo.opengl.objmodel;

import android.opengl.GLES10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class Model {
    List<Float> verticesList = new ArrayList<Float>();
    List<Float> textureList = new ArrayList<Float>();

    public Model(List<Float> verticesList, List<Float> textureList) {

        this.verticesList = verticesList;
        this.textureList = textureList;

        //顶点坐标
        if (vertextBuffer == null) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(verticesList.size() * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            vertextBuffer = byteBuffer.asFloatBuffer();
        }
        vertextBuffer.clear();
        for (Float f : verticesList) {
            vertextBuffer.put(f);
        }
        vertextBuffer.position(0);

        //纹理坐标
        if (textureBuffer == null) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(textureList.size() * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            textureBuffer = byteBuffer.asFloatBuffer();
        }
        textureBuffer.clear();
        for (Float f : textureList) {
            textureBuffer.put(f);
        }
        textureBuffer.position(0);

    }

    public void setTextureId(int id) {
        this.tid = id;
    }


    private FloatBuffer vertextBuffer;
    private FloatBuffer textureBuffer;
    private int tid = 0;

    private float[] translate_vector = new float[4];
    private float SCALE = 0.005F;// 缩放暂时使用这个

    public void update(float[] translate_vector, float scale) {
        this.translate_vector = translate_vector;
        this.SCALE = scale;
//        update();
    }


    public void draw() {

        GLES10.glPushMatrix();

        //平移到地图指定位置
        GLES10.glTranslatef(translate_vector[0], translate_vector[1], translate_vector[2]);
        //缩放物体大小适应地图
        GLES10.glScalef(SCALE, SCALE, SCALE);
        GLES10.glRotatef(90, 1,0,0);

        GLES10.glEnable(GLES10.GL_DEPTH_TEST);


        GLES10.glEnable(GLES10.GL_TEXTURE_2D);
        GLES10.glEnable(GLES10.GL_BLEND);
        GLES10.glTexEnvf(GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, GLES10.GL_MODULATE);
        GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
        GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1);

        //开启顶点缓冲区
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        //开启纹理缓冲区
        GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, tid);

        //顶点指针
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertextBuffer);

        GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT,0,textureBuffer);


        //开始画
        GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, verticesList.size() / 3 );// 3个值为一个点


        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

        GLES10.glDisable(GLES10.GL_BLEND);
        GLES10.glDisable(GLES10.GL_DEPTH_TEST);

        GLES10.glPopMatrix();
        GLES10.glFlush();

    }




}
