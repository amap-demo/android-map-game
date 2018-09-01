package com.amap.map3d.demo.opengl.common;

import android.opengl.GLES20;

/**
 * @author zxy
 * @data 1/9/18
 */

public class GLShaderManager {


    TextureShader textureShader;


    public TextureShader getTextureShader() {
        if(textureShader == null) {
            textureShader = new TextureShader();
        }
        return textureShader;
    }
    
    public void destroy() {
        if(textureShader != null) {
            textureShader.destroy();
        }
    }
    


    public static class TextureShader {
        String vertexShader = "precision highp float;\n" +
                "        attribute vec3 aVertex;//顶点数组,三维坐标\n" +
                "        attribute vec2 aTexture;//颜色数组,四维坐标\n" +
                "        uniform mat4 aMVPMatrix;//mvp矩阵\n" +
                "        varying vec2 texture;//\n" +
                "        void main(){\n" +
                "            gl_Position = aMVPMatrix * vec4(aVertex, 1.0);\n" +
                "            texture = aTexture;\n" +
                "        }";

        String fragmentShader =
                "        precision highp float;\n" +
                        "        varying vec2 texture;//\n" +
                        "        uniform sampler2D aTextureUnit0;//纹理id\n" +
                        "        uniform vec4 aColor;//颜色数组,四维坐标\n" +
                        "        void main(){\n" +
                        "            gl_FragColor = texture2D(aTextureUnit0, texture);\n" +
                        "        }";

        public int aVertex,aMVPMatrix,aTexture,aColor;
        public int program;

        public void create() {
            int vertexLocation = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            int fragmentLocation = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

            GLES20.glShaderSource(vertexLocation,vertexShader);
            GLES20.glCompileShader(vertexLocation);

            GLES20.glShaderSource(fragmentLocation,fragmentShader);
            GLES20.glCompileShader(fragmentLocation);

            program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program,vertexLocation);
            GLES20.glAttachShader(program,fragmentLocation);
            GLES20.glLinkProgram(program);


            aVertex  = GLES20.glGetAttribLocation(program, "aVertex");
            aTexture = GLES20.glGetAttribLocation(program,"aTexture");
            aMVPMatrix = GLES20.glGetUniformLocation(program,"aMVPMatrix");
            aColor = GLES20.glGetUniformLocation(program,"aColor");

        }

        public void destroy() {
        }
    }
}
