package com.amap.map3d.demo.opengl.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.amap.api.maps.model.BitmapDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

/**
 * @author zxy
 * @data 1/9/18
 */

public class GLTextureManager {

    private Context context;
    private int densityDpi = 1;

    private Hashtable<BitmapDescriptor,TextureItem> textureItemHashtable = new Hashtable<BitmapDescriptor,TextureItem>();

    public GLTextureManager(Context context) {
        this.context = context;
        densityDpi = (int) context.getResources().getDisplayMetrics().density;
    }


    public TextureItem getTextureItem(BitmapDescriptor bitmapDescriptor) {

        synchronized (textureItemHashtable) {
            if (bitmapDescriptor != null) {
                if (textureItemHashtable.containsKey(bitmapDescriptor)) {
                    return textureItemHashtable.get(bitmapDescriptor);
                }
                TextureItem textureItem = loadGLTexture(context,bitmapDescriptor);
                if(textureItem != null) {
                    textureItemHashtable.put(bitmapDescriptor, textureItem);
                    return textureItem;
                }
            }
        }
        return null;
    }


    public void destroy() {
        synchronized (textureItemHashtable) {
            for (Hashtable.Entry<BitmapDescriptor, TextureItem> entry : textureItemHashtable.entrySet()) {
                if (entry.getKey() != null) {
                    entry.getKey().recycle();
                }
            }
            textureItemHashtable.clear();
        }
    }


    /**
     * Load the textures
     *
     * @param context - The ParticleActivity context
     */
    private TextureItem loadGLTexture(Context context, BitmapDescriptor bitmapDescriptor) {

        TextureItem textureItem = new TextureItem();
        int[] textures = new int[1];

        if (context != null ) {
            Bitmap bitmap = null;
            bitmap = bitmapDescriptor.getBitmap();

            textureItem.setOriWidth(bitmap.getWidth() * densityDpi);
            textureItem.setOriHeight(bitmap.getHeight() * densityDpi);

            //生成id，n为参数个数，textures生成之后存放的位置
            GLES20.glGenTextures(1, textures, 0);


            textureItem.setTextureID(textures[0]);

            //加载纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }


        return textureItem;
    }



}
