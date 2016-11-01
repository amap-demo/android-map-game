package com.amap.map3d.demo.opengl.particle;

import android.opengl.GLES10;

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
            0, 0, 1.0f,
            0, 1, 1.0f,
            1, 0, 1.0f,
            1, 1, 1.0f,
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

    public ArrayList<ParticlePoint> particles = new ArrayList<>();


    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    private Random random;

    public ParticleSystem() {

        random = new Random(System.currentTimeMillis());

        for (int i = 0; i < PARTICLECOUNT; i++) {
            ParticlePoint particle = new ParticlePoint();

            particle.setPosition(random.nextFloat(), random.nextFloat(), random.nextFloat());
            particle.life = 1.0f;
            particle.brightness = (random.nextFloat() * 100.0f) / 700.0f + 0.003f;
            particle.setColor(colors[i * (12 / PARTICLECOUNT)][0],
                    colors[i * (12 / PARTICLECOUNT)][1],
                    colors[i * (12 / PARTICLECOUNT)][2],
                    1);
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

        //定点坐标
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
    private float SCALE = 0.005F;// 缩放暂时使用这个

    /**
     * 地图坐标系变换的时候会调用
     *
     * @param translate_vector，需要平移的距离
     * @param scale                    整体缩放啊的比例
     */
    public void updateReference(float[] translate_vector, float scale) {
        this.translate_vector = translate_vector;
        this.SCALE = scale;
    }

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
            // apply a gravity to the z speed, in this case
//            particlePoint.vel[1] -= (GRAVITY * timeFrame);

            // move the particle according to it's speed
            particlePoint.pos[0] += particlePoint.vel[0] * timeFrame;
            particlePoint.pos[1] += particlePoint.vel[1] * timeFrame;
            particlePoint.pos[2] += particlePoint.vel[2] * timeFrame;

            // Reduce Particles Life By 'brightness'
            particlePoint.life -= particlePoint.brightness;

            // if the particle has died, respawn it
            if (particlePoint.life < 0.0f)
                initParticle(i);
        }
    }

    private void initParticle(int i) {
        ParticlePoint particlePoint = particles.get(i);
        particlePoint.life = 1.0f;
        particlePoint.brightness = (random.nextFloat() * 100.0f) / 700.0f + 0.003f;

        // loop through all the particles and create new instances of each one
        particlePoint.setPosition(0, 0, 0);

        // random x and z speed between -1.0 and 1.0
        //random y speed between 4.0 and 7.0
//        particlePoint.setVelocity((random.nextFloat() * 2.0f) - 1.0f,
//                (random.nextFloat() * 3.0f) - 4.0f,
//                (random.nextFloat() * 2.0f) - 1.0f);
        particlePoint.setVelocity((random.nextFloat() * 2.0f) - 1.0f,
                (random.nextFloat() * 3.0f) - 4.0f,
                (random.nextFloat() * 2.0f) - 1.0f);

//        particlePoint.setVelocity((float)((5 * Math.random())-2.6f)*1.5f,
//                                  (float)((5 * Math.random())-2.5f)*1.5f,
//                                  (float)((5 * Math.random())-2.5f)*1.5f);
        particlePoint.setColor(colors[col][0],
                colors[col][1],
                colors[col][2],
                1);
    }


    public void draw() {

        GLES10.glPushMatrix();

        //平移到地图指定位置
        GLES10.glTranslatef(translate_vector[0], translate_vector[1], translate_vector[2]);
        //缩放物体大小适应地图
        GLES10.glScalef(SCALE, SCALE, SCALE);

//        long time = SystemClock.uptimeMillis() % 4000L;
//        float angle = 0.090f * ((int) time);
//        GLES10.glRotatef(angle, 1, 1, 1);

        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

        //启用纹理
        GLES10.glEnable(GLES10.GL_TEXTURE_2D);
        //绑定纹理
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureId);

        //顶点指针
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, mVertexBuffer);
        //指定纹理坐标指针
        GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, mTextureBuffer);

        //Set the face rotation
        GLES10.glFrontFace(GLES10.GL_CCW);

        // 开始画
        for (ParticlePoint particlePoint : particles) {
            GLES10.glPushMatrix();
            float[] color = particlePoint.color;
            GLES10.glColor4f(color[0], color[1], color[2], particlePoint.life);

            float[] pos = particlePoint.pos;
            GLES10.glTranslatef(pos[0], pos[1], pos[2]);
//            GLES10.glDrawElements(GLES10.GL_TRIANGLES, mIndexBuffer.capacity(), GLES10.GL_UNSIGNED_BYTE, mIndexBuffer);
              GLES10.glDrawElements(GLES10.GL_TRIANGLES, indices.length, GLES10.GL_UNSIGNED_SHORT, mIndexBuffer);
            GLES10.glPopMatrix();
        }

        GLES10.glDisable(GLES10.GL_TEXTURE_2D);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);


        GLES10.glPopMatrix();
        GLES10.glFlush();


        if (delay > 25) {
            col++;
            delay = 0;

            if (col > 11)
                col = 0;
        }

        delay++;

    }


}
