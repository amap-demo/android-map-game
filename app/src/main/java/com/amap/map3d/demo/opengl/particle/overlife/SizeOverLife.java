package com.amap.map3d.demo.opengl.particle.overlife;

/**
 * @author zxy
 * @data 2/9/18
 */
public abstract class SizeOverLife {

    public final int DEFAULT_SIZE = 0;

    public abstract float getSizeX(float timeFrame);
    public abstract float getSizeY(float timeFrame);
    public abstract float getSizeZ(float timeFrame);
}
