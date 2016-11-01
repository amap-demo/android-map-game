package com.amap.map3d.demo.opengl.ms3dmodel.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by LAN on 2015/7/6.
 */
public class Utils {
	public static FloatBuffer getFloatBuffer(float[] vertexData) {
		FloatBuffer floatBuffer = ByteBuffer
				.allocateDirect(vertexData.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(vertexData);

		return floatBuffer;
	}
}
