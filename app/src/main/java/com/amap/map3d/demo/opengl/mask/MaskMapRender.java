package com.amap.map3d.demo.opengl.mask;

import android.opengl.GLES20;
import android.opengl.Matrix;

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

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

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

    float[] mvp = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {


        Matrix.setIdentityM(mvp, 0);

        GLES20.glUseProgram(shader.program);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_DST_COLOR);
        GLES20.glUniform4fv(shader.aColor,1, color, 0);

        GLES20.glEnableVertexAttribArray(shader.aVertex);
        GLES20.glVertexAttribPointer(shader.aVertex,3,GLES20.GL_FLOAT,false,0,mVertexBuffer);


        GLES20.glUniformMatrix4fv(shader.aMVPMatrix,1,false,mvp,0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(shader.aVertex);

        GLES20.glDisable(GLES20.GL_BLEND);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initShader();
    }

    @Override
    public void OnMapReferencechanged() {

    }



    private class MyShader {
        String vertexShader = "precision highp float;\n" +
                "        attribute vec3 aVertex;//顶点数组,三维坐标\n" +
                "        uniform vec4 aColor;//颜色数组,四维坐标\n" +
                "        uniform mat4 aMVPMatrix;//mvp矩阵\n" +
                "        varying vec4 color;//\n" +
                "        void main(){\n" +
                "            gl_Position = aMVPMatrix * vec4(aVertex, 1.0);\n" +
                "            color = aColor;\n" +
                "        }";

        String fragmentShader =
                "        precision highp float;\n" +
                "        varying vec4 color;//\n" +
                "        void main(){\n" +
                "            gl_FragColor = color;\n" +
                "        }";

        int aVertex,aMVPMatrix,aColor;
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
            aMVPMatrix = GLES20.glGetUniformLocation(program,"aMVPMatrix");
            aColor = GLES20.glGetUniformLocation(program,"aColor");

        }
    }

    MyShader shader;

    public void initShader() {
        shader = new MyShader();
        shader.create();
    }
}
