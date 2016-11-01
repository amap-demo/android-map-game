package com.amap.map3d.demo.opengl.ms3dmodel.program;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;

import com.amap.map3d.demo.opengl.ms3dmodel.util.ShaderHelper;
import com.amap.map3d.demo.opengl.ms3dmodel.util.TextResourceReader;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

public class ShaderProgram {
	
	private static final String TAG = "ShaderProgram";
	
    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_ALPHA = "u_Alpha";
   
    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    
    protected final int uAlphaLocation;
    protected final int uTextureUnitLocation;
    protected final int uColorLocation;
    protected final int uMatrixLocation;
    
    protected final int aPositionLocation;
    protected final int aColorLocation;
    protected final int aTextureCoordinates;

    // Shader mProgram
    protected final int mProgram;

    public ShaderProgram(Context context, int vertexShaderResourceId,
                         int fragmentShaderResourceId) {
        // Compile the shaders and link the mProgram.
        mProgram = ShaderHelper.buildProgram(
                TextResourceReader
                        .readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader
                        .readTextFileFromResource(context, fragmentShaderResourceId));
        
        uAlphaLocation = GLES20.glGetUniformLocation(mProgram, U_ALPHA);
        uTextureUnitLocation = GLES20.glGetUniformLocation(mProgram, U_TEXTURE_UNIT);
        uColorLocation = GLES20.glGetUniformLocation(mProgram, U_COLOR);
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, U_MATRIX);
        
        aPositionLocation = GLES20.glGetAttribLocation(mProgram, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(mProgram, A_COLOR);
        aTextureCoordinates = GLES20.glGetAttribLocation(mProgram, A_TEXTURE_COORDINATES);

    }        

    public void useProgram() {
        // Set the current OpenGL shader mProgram to this mProgram.
    	GLES20.glUseProgram(mProgram);
    }
    
    public void setAlpha(float alpha) {
    	if(uAlphaLocation != -1){
        	GLES20.glUniform1f(uAlphaLocation, alpha);
    	}
    }
    
    public void setColor(int color){
    	if(uColorLocation != -1){
	    	GLES20.glUniform4f(uColorLocation, Color.red(color) / 255f,
                    Color.green(color) / 255f, Color.blue(color) / 255f, Color.alpha(color) / 255f);
    	}
    }
    
    public void setMatrix(float[] matrix) {
    	if(uMatrixLocation != -1){
    		GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    	}
    }
    
    public void setTexture0(int texture) {
        if(uTextureUnitLocation != -1){
        	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        	GLES20.glUniform1i(uTextureUnitLocation, 0);
    	}
    }
    
    public void setPositionAttribute(FloatBuffer positionBuffer, int componentCount) {
//    	Log.d(TAG, "setPositionAttribute -> aPositionLocation = " + aPositionLocation);
    	if(aPositionLocation != -1 && positionBuffer != null){
//    		Log.d(TAG, "setPositionAttribute -> positionBuffer = " + positionBuffer);
//    		Log.d(TAG, "setPositionAttribute -> componentCount = " + componentCount);
    		setVertexAttribPointer(positionBuffer, aPositionLocation, componentCount);
    	}
    }
    
    public void setColorAttribute(FloatBuffer colorBuffer, int componentCount) {
//    	Log.d(TAG, "setColorAttribute -> aColorLocation = " + aColorLocation);
    	if(aColorLocation != -1 && colorBuffer != null){
//    		Log.d(TAG, "setColorAttribute -> colorBuffer = " + colorBuffer);
//    		Log.d(TAG, "setColorAttribute -> componentCount = " + componentCount);
    		setVertexAttribPointer(colorBuffer, aColorLocation, componentCount);
    	}
    }
    
    public void setTextureAttribute(FloatBuffer textureBuffer, int componentCount) {
    	if(aTextureCoordinates != -1 && textureBuffer != null){
    		setVertexAttribPointer(textureBuffer, aTextureCoordinates, componentCount);
    	}
    }

	protected void setVertexAttribPointer(FloatBuffer floatBuffer,
			int attributeLocation, int componentCount) {
		floatBuffer.position(0);
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
				false, componentCount * 4, floatBuffer);
		glEnableVertexAttribArray(attributeLocation);
	}
    
}
