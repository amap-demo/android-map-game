package com.amap.map3d.demo.opengl.particle.overlife;

import java.util.Random;

/**
 *
 * 大小根据曲线变化
 * @author zxy
 * @data 2/9/18
 */
public class CurveSizeOverLife extends SizeOverLife{

    Random random = new Random();

    float k = 0.005f;
    public CurveSizeOverLife() {

    }

    // y = x + 1

    @Override
    public float getSizeX(float timeFrame) {

        return k;
    }

    @Override
    public float getSizeY(float timeFrame) {
        return k;
    }

    @Override
    public float getSizeZ(float timeFrame) {
        return DEFAULT_SIZE;
    }
}
