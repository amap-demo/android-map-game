package com.amap.map3d.demo.opengl.particle;

import java.util.Random;

/**
 * @author zxy
 * @data 1/9/18
 */

public class SinglePointParticleShape extends ParticleShapeModule {

    float[] point_3 = new float[3];


    public SinglePointParticleShape(float x, float y ,float z) {
        point_3[0] = x;
        point_3[1] = y;
        point_3[2] = z;
    }

    @Override
    public float[] getPoint() {
        return point_3;
    }
}
