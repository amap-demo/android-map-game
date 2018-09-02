package com.amap.map3d.demo.opengl.particle;

/**
 * 粒子系统分组
 *
 * @author zxy
 * @data 1/9/18
 */
public class ParticleEmissonModule {
    private int rate;
    private int rateTime;
    private float lautchOffset;

    /**
     * rateTime内发射rate个粒子
     * @param rate 发射数量
     * @param rateTime 间隔时间
     */
    public ParticleEmissonModule(int rate, int rateTime) {
        this.rate = rate;
        this.rateTime = rateTime;
        this.lautchOffset = this.rateTime * 1.0f / rate;
    }

    public int getRate() {
        return this.rate;
    }

    /**
     * 发射间隔
     * @return
     */
    public int getRateTime() {
        return rateTime;
    }

    public float getLaunchOffset() {
        return lautchOffset;
    }
}
