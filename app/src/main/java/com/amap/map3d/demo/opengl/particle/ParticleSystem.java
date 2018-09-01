package com.amap.map3d.demo.opengl.particle;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Random;

class ParticleSystem {
    public final int PARTICLECOUNT = 300;
    public float GRAVITY = 10.0f;
    public float PSIZE = 1f;

    float vertices[] = {
            0 - 0.5f, 0 - 0.5f, 1f,
            0 - 0.5f, 1 - 0.5f, 1f,
            1 - 0.5f, 0 - 0.5f, 1f,
            1 - 0.5f, 1 - 0.5f, 1f,
    };


    float textureUV[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    short indices[] = {
            0, 1, 3,
            0, 3, 2
    };

    public FloatBuffer mVertexBuffer;
    public FloatBuffer mTextureBuffer;
    public ShortBuffer mIndexBuffer;

    private int textureId;

    public final float colors[][] =
            {
                    {1.0f, 0.5f, 0.5f}, {1.0f, 0.75f, 0.5f}, {1.0f, 1.0f, 0.5f}, {0.75f, 1.0f, 0.5f},
                    {0.5f, 1.0f, 0.5f}, {0.5f, 1.0f, 0.75f}, {0.5f, 1.0f, 1.0f}, {0.5f, 0.75f, 1.0f},
                    {0.5f, 0.5f, 1.0f}, {0.75f, 0.5f, 1.0f}, {1.0f, 0.5f, 1.0f}, {1.0f, 0.5f, 0.75f}
            };

    public ArrayList<ParticlePoint> particles = new ArrayList<ParticlePoint>();


    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    private Random random;

    public ParticleSystem() {

        random = new Random(System.currentTimeMillis());

        for (int i = 0; i < PARTICLECOUNT; i++) {
            ParticlePoint particle = new ParticlePoint();
            setUpParticlePoint(particle);
            particles.add(particle);
        }


//        //index
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(indices.length * 2);
        byteBuffer2.order(ByteOrder.nativeOrder());
        mIndexBuffer = byteBuffer2.asShortBuffer();
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);

//        //index
//        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
//        mIndexBuffer.order(ByteOrder.nativeOrder());
//        mIndexBuffer.put(indices);
//        mIndexBuffer.position(0);

        //顶点坐标
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        //texture
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(textureUV.length * 4);
        byteBuffer1.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuffer1.asFloatBuffer();
        mTextureBuffer.put(textureUV);
        mTextureBuffer.position(0);

    }

    private float[] translate_vector = new float[4];
    private float SCALE = 0.1F;// 缩放暂时使用这个


    long mLastTime = 0L;
    public int col;
    public int delay;

    // update the particle system, move everything
    public void updateParticle() {
        // calculate time between frames in seconds
        long currentTime = System.currentTimeMillis();
        float timeFrame = (currentTime - mLastTime) / 1000f;

        mLastTime = currentTime;

        // move the particles
        for (int i = 0; i < PARTICLECOUNT; i++) {
            ParticlePoint particlePoint = particles.get(i);

            // 粒子运动方式
            // move the particle according to it's speed
            particlePoint.pos[0] += particlePoint.vel[0] * timeFrame;
            particlePoint.pos[1] += particlePoint.vel[1] * timeFrame;
            particlePoint.pos[2] += particlePoint.vel[2] * timeFrame;

//            particlePoint.life -= timeFrame;

            if(particlePoint.pos[1] < -1) {
                initParticle(i);
            }
        }
    }

    private void setUpParticlePoint(ParticlePoint particlePoint) {
        if (particlePoint != null) {

            // 初始化点
            particlePoint.setPosition(random.nextFloat() * 2 - 1, random.nextFloat() / 10 + 1.5f, 0);
            particlePoint.life = random.nextFloat() * 2 + 2.0f;
//            particlePoint.brightness = (random.nextFloat() * 100.0f) / 700.0f + 0.003f;
//            particlePoint.brightness = 0.01f;
            particlePoint.setColor(1,1,1,1);
//            particlePoint.setVelocity((random.nextFloat() * 2.0f) - 1.0f,
//                    (random.nextFloat() * 3.0f) - 4.0f,
//                    0);
            // rain
            particlePoint.setVelocity(-0.1f, -(random.nextFloat()  + 1.0f), 0);
            // snow
//            particlePoint.setVelocity(random.nextFloat() * 0.1f, -(random.nextFloat() * 0.1f + 0.1f), 0);

        }
    }

    private void initParticle(int i) {
        ParticlePoint particlePoint = particles.get(i);
        setUpParticlePoint(particlePoint);

    }



    public void draw(float[] mvp) {

        checkGlError("particle system  before draw");

        GLES20.glUseProgram(shader.program);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendColor(1.0f, 1.0f, 1.0f, 1);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glEnableVertexAttribArray(shader.aTexture);
        GLES20.glVertexAttribPointer(shader.aTexture,2,GLES20.GL_FLOAT, false, 2 * 4,mTextureBuffer);

        GLES20.glEnableVertexAttribArray(shader.aVertex);
        GLES20.glVertexAttribPointer(shader.aVertex,3,GLES20.GL_FLOAT, false, 3 * 4,mVertexBuffer);


        GLES20.glFrontFace(GLES20.GL_CCW);


        // 开始画
        for (ParticlePoint particlePoint : particles) {
            float[] mvpMatrix = mvp.clone();
            float[] color = particlePoint.color;
            GLES20.glUniform4f(shader.aColor,color[0], color[1], color[2], particlePoint.life);

            float[] pos = particlePoint.pos;
            Matrix.translateM(mvpMatrix,0,pos[0], pos[1], pos[2]);
            GLES20.glUniformMatrix4fv(shader.aMVPMatrix,1,false,mvpMatrix,0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
            checkGlError("glDrawElements");
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(shader.aVertex);
        GLES20.glDisableVertexAttribArray(shader.aTexture);
        GLES20.glUseProgram(0);


        checkGlError("particleSystem");


        if (delay > 25) {
            col++;
            delay = 0;

            if (col > 11)
                col = 0;
        }

        delay++;

    }

    public void destroy() {
        shader = null;
    }

    public void setTextureSize(float textureWidth, float textureHeight) {


        for (int i = 0; i < vertices.length / 3; i++) {
            vertices[i * 3 + 0] *= textureWidth;
            vertices[i * 3 + 1] *= textureHeight;

        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

    }


    static class MyShader {
        String vertexShader = "precision highp float;\n" +
                "        attribute vec3 aVertex;//顶点数组,三维坐标\n" +
                "        attribute vec2 aTexture;//颜色数组,四维坐标\n" +
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
                        "        uniform vec4 aColor;//颜色数组,四维坐标\n" +
                        "        void main(){\n" +
                        "            gl_FragColor = texture2D(aTextureUnit0, texture);\n" +
                        "        }";

        int aVertex,aMVPMatrix,aTexture,aColor;
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
            aColor = GLES20.glGetUniformLocation(program,"aColor");

        }
    }

    static MyShader shader;

    public static void initShader() {
        checkGlError("before init shaders1");
        shader = new MyShader();
        shader.create();
        checkGlError("init shaders");
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("amap", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


}
