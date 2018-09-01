package com.amap.map3d.demo.opengl.particle;

public class ParticlePoint {

    public float life;

    public float brightness;

    // Position.
    public float[] pos = new float[3];

    // Velocity.
    public float[] vel = new float[3];

    // Color
    public float[] color = new float[4];

    public void setPosition(float[] position) {
        pos[0] = position[0];
        pos[1] = position[1];
        pos[2] = position[2];
    }

    public void setPosition(float x, float y, float z) {
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
    }

    public void setVelocity(float x, float y, float z) {
        vel[0] = x;
        vel[1] = y;
        vel[2] = z;
    }

    public void setVelocity(float x, float y) {
        vel[0] = x;
        vel[1] = y;
    }

    public void setColor(float r, float g, float b, float a) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

}