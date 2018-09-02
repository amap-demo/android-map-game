package com.amap.map3d.demo.opengl.particle.overlife;

import java.util.Random;

/**
 *
 * 生命周期内的一些属性变化
 * 速度变化
 * 角度变化等等
 *
 * @author zxy
 * @data 2/9/18
 */
public class ParticleOverLifeModule {

    private VelocityOverLife overLife = null;

    /**
     * 粒子速度
     */
    float[] velocity = new float[3];

    public ParticleOverLifeModule() {
        velocity[0] = 1;
        velocity[1] = 1;
        velocity[1] = 1;
    }

    public void setVelocityOverLife(VelocityOverLife overLife) {
        this.overLife = overLife;
    }


    /**
     * 获取各个方向上的速度
     * @return
     */
    public float[] getVelocity() {
        if(overLife != null) {
            velocity[0] = overLife.getX();
            velocity[1] = overLife.getY();
            velocity[2] = overLife.getZ();
        }
        return velocity;
    }





}
