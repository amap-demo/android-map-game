package com.amap.map3d.demo.opengl.particle.overlife;

import java.util.Random;

public class RandomVelocityBetweenTwoConstants extends VelocityOverLife{

    private float x1;
    private float y1;
    private float z1;
    private float x2;
    private float y2;
    private float z2;

    private Random random;

    public RandomVelocityBetweenTwoConstants(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;

        random = new Random();
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public float getX() {
        return random.nextFloat() * (x2 - x1) + x1 ;
    }

    @Override
    public float getY() {
            return random.nextFloat() * (y2 - y1) + y1 ;
        }

    @Override
    public float getZ() {
        return random.nextFloat() * (z2 - z1) + z1 ;
    }
}
