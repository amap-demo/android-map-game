package com.amap.map3d.demo.opengl.particle.overlife;

/**
 * @author zxy
 * @data 2/9/18
 */

public class ConstantRotationOverLife extends RotationOverLife {

    private float rotate = 0;

    public ConstantRotationOverLife(float rotate) {
        this.rotate = rotate;
    }

    @Override
    public float getRotate() {
        return this.rotate;
    }
}
