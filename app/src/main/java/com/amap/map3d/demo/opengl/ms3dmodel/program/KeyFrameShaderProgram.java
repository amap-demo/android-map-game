package com.amap.map3d.demo.opengl.ms3dmodel.program;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class KeyFrameShaderProgram extends ShaderProgram {
	private static final String TAG = "KeyFrameShaderProgram";

	protected static final String U_FACTOR = "u_factor";

	protected final int uFactorLocation;

	protected final int[] aKeyPositionLocation = new int[7];

    public KeyFrameShaderProgram(Context context, int vertexShaderResourceId,
								 int fragmentShaderResourceId) {
		super(context, vertexShaderResourceId, fragmentShaderResourceId);

		uFactorLocation = GLES20.glGetUniformLocation(mProgram, U_FACTOR);

		for (int i = 0;i < 7;i++) {
			aKeyPositionLocation[i] = GLES20.glGetAttribLocation(mProgram, A_POSITION + i);
		}
    }

	public void setFactor(float factor) {
		if(uFactorLocation != -1){
			GLES20.glUniform1f(uFactorLocation, factor);
		}
	}

	public void setKeyPositionAttribute(FloatBuffer positionBuffer, int componentCount, int keyIndex) {
		if(aKeyPositionLocation[keyIndex] != -1 && positionBuffer != null){
			setVertexAttribPointer(positionBuffer, aKeyPositionLocation[keyIndex], componentCount);
		}
	}
}
