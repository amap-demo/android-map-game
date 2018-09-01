package com.amap.map3d.demo.opengl.particle;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.map3d.demo.opengl.common.GLShaderManager;
import com.amap.map3d.demo.opengl.common.GLTextureManager;
import com.amap.map3d.demo.opengl.common.TextureItem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Random;

class ParticleSystem {
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

    public ArrayList<ParticlePoint> particles = new ArrayList<ParticlePoint>();
    private com.amap.map3d.demo.opengl.common.GLShaderManager gLShaderManager;
    private com.amap.map3d.demo.opengl.common.GLTextureManager glTextureManager;
    private int width;
    private int height;
    private BitmapDescriptor texture;
    private boolean isLoadTexture = false;
    private TextureItem textureItem;

    private Random random;
    private int maxParticles;
    private int duration;
    private boolean isInit;
    private boolean loop;
    private ParticleShape particleShape;

    public ParticleSystem() {

        random = new Random(System.currentTimeMillis());
//        //index
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

        //texture
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(textureUV.length * 4);
        byteBuffer1.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuffer1.asFloatBuffer();
        mTextureBuffer.put(textureUV);
        mTextureBuffer.position(0);

    }
    long mLastTime = 0L;

    /**
     * 更新每个粒子的位置
     */
    private void updateParticle() {
        // calculate time between frames in seconds
        long currentTime = System.currentTimeMillis();
        float timeFrame = (currentTime - mLastTime) / 1000f;

        mLastTime = currentTime;

        // move the particles
        for (int i = 0; i < maxParticles; i++) {
            ParticlePoint particlePoint = particles.get(i);

            if(particlePoint.life < 0) {
                continue;
            }

            // 粒子运动方式
            // move the particle according to it's speed
            particlePoint.pos[0] += particlePoint.vel[0] * timeFrame;
            particlePoint.pos[1] += particlePoint.vel[1] * timeFrame;
            particlePoint.pos[2] += particlePoint.vel[2] * timeFrame;
            particlePoint.life -= timeFrame * 1000;

            //如果是循环的则循环处理
            if(loop) {
                if(particlePoint.life < 0) {
                    setUpParticlePoint(particlePoint);
                }
            }

        }
    }

    private void setUpParticlePoint(ParticlePoint particlePoint) {
        if (particlePoint != null) {
            particlePoint.setPosition(particleShape.getPoint());
//            particlePoint.setPosition(random.nextFloat() * 2 - 1, random.nextFloat() / 10 + 1.5f, 0);
            particlePoint.life = duration;
            particlePoint.setColor(1,1,1,1);
            // rain
            particlePoint.setVelocity(-0.1f, -(random.nextFloat()  + 1.0f), 0);
//            particlePoint.setVelocity(0,0, 0);
            // snow
//            particlePoint.setVelocity(random.nextFloat() * 0.1f, -(random.nextFloat() * 0.1f + 0.1f), 0);

        }
    }

    public void draw(float[] mvp) {


        if(!isLoadTexture) {
            textureItem = glTextureManager.getTextureItem(texture);
            if(textureItem != null) {
                isLoadTexture = true;

                // 更新buffer
                setTextureSize(textureItem.getOriWidth() * 1.0f / width , textureItem.getOriHeight() * 1.0f / height);

            }
        }

        if(textureItem == null) {
            return;
        }

        if(shader == null) {
            initShader();
        }

        if(!isInit) {
            initParticle();
            isInit = true;
        }

        updateParticle();


        checkGlError("particle system  before draw");

        GLES20.glUseProgram(shader.program);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendColor(1.0f, 1.0f, 1.0f, 1);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureItem.getTextureID());
        GLES20.glEnableVertexAttribArray(shader.aTexture);
        GLES20.glVertexAttribPointer(shader.aTexture,2,GLES20.GL_FLOAT, false, 2 * 4,mTextureBuffer);

        GLES20.glEnableVertexAttribArray(shader.aVertex);
        GLES20.glVertexAttribPointer(shader.aVertex,3,GLES20.GL_FLOAT, false, 3 * 4,mVertexBuffer);


        GLES20.glFrontFace(GLES20.GL_CCW);


        // 开始画
        for (ParticlePoint particlePoint : particles) {
            if(particlePoint.life < 0) {
                continue;
            }
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
    }

    private void initParticle() {

        for (int i = 0; i < maxParticles; i++) {
            ParticlePoint particle = new ParticlePoint();
            setUpParticlePoint(particle);
            particles.add(particle);
        }
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

    public void setgLShaderManager(GLShaderManager gLShaderManager) {
        this.gLShaderManager = gLShaderManager;
    }

    public void setGlTextureManager(GLTextureManager glTextureManager) {
        this.glTextureManager = glTextureManager;
    }

    public void setTexture(BitmapDescriptor texture) {
        this.texture = texture;
        isLoadTexture = false;
    }

    public void setShownSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    GLShaderManager.TextureShader shader;

    private void initShader() {
        checkGlError("before init shaders1");
        if(shader ==null) {
            shader = gLShaderManager.getTextureShader();
            shader.create();
        }
        checkGlError("init shaders");
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("amap", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
        this.isInit = false;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setParticleShape(ParticleShape particleShape) {
        this.particleShape = particleShape;
    }
}
