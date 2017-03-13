package com.amap.map3d.demo.opengl.objmodel;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.Matrix;

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



    public void draw(float[] mvp) {

        //        GLES10.glRotatef(90, 1,0,0);
        Matrix.rotateM(mvp,0,90,1,0,0);

        GLES20.glUseProgram(shader.program);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);


        GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
        GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tid);
        GLES20.glEnableVertexAttribArray(shader.aTexture);
        GLES20.glVertexAttribPointer(shader.aTexture,2,GLES20.GL_FLOAT, false, 2 * 4,textureBuffer);

        GLES20.glEnableVertexAttribArray(shader.aVertex);
        GLES20.glVertexAttribPointer(shader.aVertex,3,GLES20.GL_FLOAT, false, 3 * 4,vertextBuffer);



        GLES20.glUniformMatrix4fv(shader.aVertex,1,false,mvp,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, verticesList.size() / 3);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisableVertexAttribArray(shader.aVertex);
        GLES20.glDisableVertexAttribArray(shader.aTexture);
        GLES20.glUseProgram(0);

//        GLES10.glPushMatrix();
//
//        //平移到地图指定位置
//        GLES10.glTranslatef(translate_vector[0], translate_vector[1], translate_vector[2]);
//        //缩放物体大小适应地图
//        GLES10.glScalef(SCALE, SCALE, SCALE);
//        GLES10.glRotatef(90, 1,0,0);
//
//        GLES10.glEnable(GLES10.GL_DEPTH_TEST);
//
//
//        GLES10.glEnable(GLES10.GL_TEXTURE_2D);
//        GLES10.glEnable(GLES10.GL_BLEND);
//        GLES10.glTexEnvf(GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, GLES10.GL_MODULATE);
//        GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
//        GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1);
//
//        //开启顶点缓冲区
//        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
//        //开启纹理缓冲区
//        GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
//
//        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, tid);
//
//        //顶点指针
//        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertextBuffer);
//
//        GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT,0,textureBuffer);
//
//
//        //开始画
//        GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, verticesList.size() / 3 );// 3个值为一个点
//
//
//        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
//        GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
//
//        GLES10.glDisable(GLES10.GL_BLEND);
//        GLES10.glDisable(GLES10.GL_DEPTH_TEST);
//
//        GLES10.glPopMatrix();
//        GLES10.glFlush();

    }




    static class MyShader {
        String vertexShader = "precision highp float;\n" +
                "        attribute vec3 aVertex;//顶点数组,三维坐标\n" +
                "        attribute vec2 aTexture;\n" +
                "        uniform mat4 aMVPMatrix;//mvp矩阵\n" +
                "        varying vec2 texture;//\n" +
                "        void main(){\n" +
                "            gl_Position = aMVPMatrix * vec4(aVertex, 1.0);\n" +
                "            texture = aTexture;\n" +
                "        }";

        String fragmentShader =
                "        precision highp float;\n" +
                        "        varying vec2 texture;//\n" +
                        "        uniform sampler2D aTextureUnit0;//纹理id\n" +
                        "        void main(){\n" +
                        "            gl_FragColor = texture2D(aTextureUnit0, texture);\n" +
                        "        }";

        int aVertex,aMVPMatrix,aTexture;
        int program;

        public void create() {
            int vertexLocation = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            int fragmentLocation = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

            GLES20.glShaderSource(vertexLocation,vertexShader);
            GLES20.glCompileShader(vertexLocation);

            GLES20.glShaderSource(fragmentLocation,fragmentShader);
            GLES20.glCompileShader(fragmentLocation);

            program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program,vertexLocation);
            GLES20.glAttachShader(program,fragmentLocation);
            GLES20.glLinkProgram(program);


            aVertex  = GLES20.glGetAttribLocation(program, "aVertex");
            aTexture = GLES20.glGetAttribLocation(program,"aTexture");
            aMVPMatrix = GLES20.glGetUniformLocation(program,"aMVPMatrix");
        }
    }

    static MyShader shader;

    public static void initShader() {
        shader = new MyShader();
        shader.create();
    }



}
