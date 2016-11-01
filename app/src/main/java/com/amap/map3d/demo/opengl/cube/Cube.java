package com.amap.map3d.demo.opengl.cube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import android.opengl.GLES10;

class Cube {
    ArrayList<Float> verticesList = new ArrayList<Float>();

    short indices[] = {
            0, 4, 5,
            0, 5, 1,
            1, 5, 6,
            1, 6, 2,
            2, 6, 7,
            2, 7, 3,
            3, 7, 4,
            3, 4, 0,
            4, 7, 6,
            4, 6, 5,
            3, 0, 1,
            3, 1, 2,
    };

    //
    float[] colors = {
            1f, 0f, 0f, 1f, // vertex 0 red
            0f, 1f, 0f, 1f, // vertex 1 green
            0f, 0f, 1f, 1f, // vertex 2 blue
            1f, 1f, 0f, 1f, // vertex 3
            0f, 1f, 1f, 1f, // vertex 4
            1f, 0f, 1f, 1f, // vertex 5
            0f, 0f, 0f, 1f, // vertex 6
            1f, 1f, 1f, 1f, // vertex 7
    };

    public Cube(float width, float height, float depth) {
        width /= 2;
        height /= 2;
        depth /= 2;

        float vertices1[] = {
                -width, -height, -depth,
                width, -height, -depth,
                width, height, -depth,
                -width, height, -depth,
                -width, -height, depth,
                width, -height, depth,
                width, height, depth,
                -width, height, depth,
        };

        for (int i = 0; i < vertices1.length; i++) {
            verticesList.add(vertices1[i]);
        }

        //index
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = byteBuffer.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);


        //color
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(colors.length * 4);
        byteBuffer1.order(ByteOrder.nativeOrder());
        colorBuffer = byteBuffer1.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        update();

    }


    private FloatBuffer vertextBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer colorBuffer;

    private float[] translate_vector = new float[4];
    private float SCALE = 0.005F;// 缩放暂时使用这个

    public void update(float[] translate_vector, float scale) {
        this.translate_vector = translate_vector;
        this.SCALE = scale;
//        update();
    }

    private void update() {
        if (vertextBuffer == null) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(verticesList.size() * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            vertextBuffer = byteBuffer.asFloatBuffer();
        }
        vertextBuffer.clear();
        for (Float f : verticesList) {
//            vertextBuffer.put(f * SCALE);
            vertextBuffer.put(f);
        }
        vertextBuffer.position(0);
    }



    public void draw() {

        GLES10.glPushMatrix();

        //平移到地图指定位置
        GLES10.glTranslatef(translate_vector[0], translate_vector[1], translate_vector[2]);
        //缩放物体大小适应地图
        GLES10.glScalef(SCALE, SCALE, SCALE);

        GLES10.glDisable(GLES10.GL_TEXTURE_2D);

        GLES10.glEnable(GLES10.GL_DEPTH_TEST);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);

        //顶点指针
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertextBuffer);

        //指定着色模式
        GLES10.glShadeModel(GLES10.GL_FLAT);

        //颜色指针
        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);

        //开始画
        GLES10.glDrawElements(GLES10.GL_TRIANGLES, indices.length, GLES10.GL_UNSIGNED_SHORT, indexBuffer);


        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);

        GLES10.glDisable(GLES10.GL_DEPTH_TEST);

        GLES10.glPopMatrix();
        GLES10.glFlush();

    }




}
