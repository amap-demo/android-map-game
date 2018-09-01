package com.amap.map3d.demo.opengl.particle;

import android.graphics.Rect;

import java.util.Random;

/**
 * @author zxy
 * @data 1/9/18
 */

public class RectParticleShape extends ParticleShape {

    private Random random = new Random();

    float[] point_3 = new float[3];
    public float left;
    public float top;
    public float right;
    public float bottom;

    int width_cal = 0;
    int height_cal = 0;



    public RectParticleShape(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        width_cal = (int) (right - left);
        height_cal = (int) (top - bottom);
    }

    @Override
    public float[] getPoint() {
        float temp_x = random.nextFloat() * (right - left) + left ;
        float temp_y = random.nextFloat() * (top - bottom) + bottom;
//        float x = random.nextFloat() * 2 - 1;
//        float y = random.nextFloat() / 10 + 1.5f;
//        float z = 0;

        float x = temp_x;
        float y = temp_y;
        float z = 0;

        point_3[0] = x;
        point_3[1] = y;
        point_3[2] = z;
        return point_3;
    }
}
