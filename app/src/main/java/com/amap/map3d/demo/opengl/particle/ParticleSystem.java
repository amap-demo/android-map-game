package com.amap.map3d.demo.opengl.particle;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.map3d.demo.opengl.common.GLShaderManager;
import com.amap.map3d.demo.opengl.common.GLTextureManager;
import com.amap.map3d.demo.opengl.common.TextureItem;
import com.amap.map3d.demo.opengl.particle.overlife.ParticleOverLifeModule;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class ParticleSystem {
    /**
     * 默认发射率，每秒发射10个， 每100ms发射一个
     */
    private static final int DEFAULT_LAUNCH_OFFSET_TIME = 100;

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
    private float ratio = 1;
    private BitmapDescriptor texture;
    private boolean isLoadTexture = false;
    private TextureItem textureItem;

    private Random random;
    private int maxParticles;

    /**
     * 当前粒子数量
     */
    private int currentParticleNum = 0;

    /**
     * 粒子系统持续时长
     */
    private long duration;

    /**
     * 每个粒子的寿命，默认是5000ms
     */
    private long particleLifeTime = 5000;

    /**
     * 每个粒子开始的速度，默认是1
     */
    private float particleStartSpeed = 1;

    /**
     * 粒子系统当前生命值，开始时和duration相同
     * 用户计算存活时间
     */
    private long particleSystemLife;
    private boolean loop;
    private ParticleShapeModule particleShapeModule;
    private ParticleEmissonModule particleEmission;


    // 记录上一次时间
    private long mLastTime = 0L;
    private ParticleOverLifeModule particleOverLifeModule;
    private boolean preWraw;


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


    /**
     * 更新每个粒子的位置
     */
    private void updateParticle(List<ParticlePoint> readyToShowPoint, float timeFrame) {
        if(readyToShowPoint == null) {
            return;
        }
        synchronized (readyToShowPoint) {
            Iterator<ParticlePoint> it = readyToShowPoint.iterator();
            while(it.hasNext()){
                ParticlePoint particlePoint = it.next();

                // 生命周期已经结束的元素直接删除
                if (!particlePoint.isAlive()) {
                    it.remove();
                    continue;
                }
                // 粒子运动状态更新,速度*各个轴速度的变化* 时间系数
                particlePoint.pos[0] += particleStartSpeed * particlePoint.vel[0] * timeFrame;
                particlePoint.pos[1] += particleStartSpeed * particlePoint.vel[1] * timeFrame;
                particlePoint.pos[2] += particleStartSpeed * particlePoint.vel[2] * timeFrame;
                particlePoint.life -= timeFrame * 1000;



            }
        }
    }

    private void setUpParticlePoint(ParticlePoint particlePoint) {
        if (particlePoint != null) {
            particlePoint.setPosition(particleShapeModule.getPoint());
            particlePoint.life = particleLifeTime;
            particlePoint.setColor(1,1,1,1);

            if(particleOverLifeModule != null) {
                float[] velocity = particleOverLifeModule.getVelocity();
                particlePoint.setVelocity(velocity[0],velocity[1],velocity[2]);
            } else {
                particlePoint.setVelocity(1,1,1);
            }

        }
    }


    List<ParticlePoint> readyToShowPoint = new ArrayList<ParticlePoint>();

    public void draw(float[] mvp) {

        if(!isLoadTexture) {
            textureItem = glTextureManager.getTextureItem(texture);
            if(textureItem != null) {
                isLoadTexture = true;

                // 更新buffer, 宽度乘上一个比例，因为投影矩阵的关系
                setTextureSize(ratio * textureItem.getOriWidth() * 1.0f / width , textureItem.getOriHeight() * 1.0f / height);

            }
        }

        if(textureItem == null) {
            return;
        }

        if(shader == null) {
            initShader();
        }

        if(shader == null) {
            return;
        }

        // calculate time between frames in seconds
        long currentTime = System.currentTimeMillis();
        float timeFrame = (currentTime - mLastTime) / 1000f;

        mLastTime = currentTime;

        if(isSystemOver(timeFrame)) {
            return;
        }

        // 准备需要绘制的粒子
        prepareParticle(readyToShowPoint,currentTime,timeFrame);

        updateParticle(readyToShowPoint, timeFrame);


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


        synchronized (readyToShowPoint) {
            // 开始画
            for (ParticlePoint particlePoint : readyToShowPoint) {
                if (!particlePoint.isAlive()) {
                    continue;
                }
                float[] mvpMatrix = mvp.clone();
                float[] color = particlePoint.color;
                GLES20.glUniform4f(shader.aColor, color[0], color[1], color[2], color[3]);

                float[] pos = particlePoint.pos;
                Matrix.translateM(mvpMatrix, 0, pos[0], pos[1], pos[2]);
                GLES20.glUniformMatrix4fv(shader.aMVPMatrix, 1, false, mvpMatrix, 0);
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
                checkGlError("glDrawElements");
            }
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(shader.aVertex);
        GLES20.glDisableVertexAttribArray(shader.aTexture);
        GLES20.glUseProgram(0);


        checkGlError("particleSystem");
    }


    /**
     * 判断是否有必要继续绘制
     * @param timeFrame
     * @return true表示不需要
     */
    private boolean isSystemOver(float timeFrame) {
        // 计算当前粒子系统生命
        particleSystemLife -= timeFrame;

        if(particleSystemLife < 0 ) {
            if(loop) {
                particleSystemLife = duration;
            } else {
                return true;
            }
        }
        return false;
    }


    private long prepareLastTime = 0L;
    /**
     * 准备需要显示的粒子
     * @param readyToShowPoint 存放当前粒子
     * @param currentTime 当前时间
     * @param timeFrame 一帧花费时间
     */
    private void prepareParticle(List<ParticlePoint> readyToShowPoint, long currentTime, float timeFrame) {

        if(readyToShowPoint == null) {
            return;
        }
        if(readyToShowPoint.size() > 0) {
            currentParticleNum = readyToShowPoint.size();

            // 粒子已经达到最大状态，不需要再最加
            if (currentParticleNum >= maxParticles) {
                return;
            }
        } else {
            currentParticleNum = 0;
        }


        //rateTime内发射rate个粒子
        //发射粒子间隔时间
        float launchOffset = DEFAULT_LAUNCH_OFFSET_TIME;
        if(particleEmission != null) {
            launchOffset = particleEmission.getLaunchOffset();
        }


        // 发射时间内，如果个数已经达到了
        if(currentTime - prepareLastTime < launchOffset) {
            return ;
        }
        prepareLastTime = currentTime;

        // * 1000 转换为毫秒
        int perLaunchCount = (int) Math.round(timeFrame * 1000.0 / launchOffset);


        for (int i = 0; i < perLaunchCount; i++) {
            // 从缓存中获取
            if(particles.size() > 0) {
                for(ParticlePoint particlePoint : particles) {
                    // 从缓存中获取生命周期已经走完的粒子
                    if(!particlePoint.isAlive()) {
                        setUpParticlePoint(particlePoint);
                        readyToShowPoint.add(particlePoint);
                        continue;
                    }
                }
            }

            // 没有找到则创建粒子
            // 创建粒子
            ParticlePoint particlePoint = new ParticlePoint();
            setUpParticlePoint(particlePoint);

            readyToShowPoint.add(particlePoint);

            // 添加到缓存目录
            particles.add(particlePoint);
        }






        
    }

    public void destroy() {
        shader = null;
        readyToShowPoint.clear();
        particles.clear();
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
        this.ratio = (float) width / height;
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


    /**
     * 最多粒子数量，不是指屏幕内会显示的粒子特效
     * @param maxParticles
     */
    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    /**
     * 设置粒子系统总时长
     * @param duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
        this.particleSystemLife = duration;
    }


    /**
     * 设置单个粒子的寿命
     * @param lifeTime
     */
    public void setParticleLifeTime(long lifeTime) {
        this.particleLifeTime = lifeTime;
    }

    /**
     * 设置粒子发射的初始速度
     * @param startSpeed
     */
    public void setParticleStartSpeed(float startSpeed) {
        this.particleStartSpeed = startSpeed;
    }


    /**
     * 是指粒子系统是否循环
     * @param loop
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * 设置发射器，即生成粒子的位置
     * @param particleShapeModule
     */
    public void setParticleShapeModule(ParticleShapeModule particleShapeModule) {
        this.particleShapeModule = particleShapeModule;
    }

    /**
     * 设置发射率，可以控制每秒发射多少个粒子
     * @param particleEmission
     */
    public void setParticleEmission(ParticleEmissonModule particleEmission) {
        this.particleEmission = particleEmission;
    }

    /**
     * 获取当前粒子数量
     * @return
     */
    public int getCurrentParticleNum() {
        return currentParticleNum;
    }

    /**
     * 设置粒子在生命周期内的变化
     * @param particleOverLifeModule
     */
    public void setParticleOverLifeModule(ParticleOverLifeModule particleOverLifeModule) {
        this.particleOverLifeModule = particleOverLifeModule;
    }

    /**
     * 是否开启预制系统，开启之后，会直接展示一个周期后的粒子效果
     * @param preWraw
     */
    public void setPreWraw(boolean preWraw) {
        this.preWraw = preWraw;
    }
}
