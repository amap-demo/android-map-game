package com.amap.map3d.demo.opengl.particle;

import android.content.Context;
import android.opengl.Matrix;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.opengl.common.GLShaderManager;
import com.amap.map3d.demo.opengl.common.GLTextureManager;
import com.amap.map3d.demo.opengl.particle.overlife.ParticleOverLifeModule;
import com.amap.map3d.demo.opengl.particle.overlife.RandomVelocityBetweenTwoConstants;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ParticleMapRender implements CustomRenderer {

    public static float SCALE = 0.005F;// 缩放暂时使用这个

    private LatLng center = new LatLng(39.90403, 116.407525);// 北京市经纬度


    private GLTextureManager glTextureManager = null;
    private GLShaderManager glShaderManager = null;

    private AMap aMap;

    private Context context;
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public ParticleMapRender(AMap aMap, Context context) {
        this.aMap = aMap;
        this.context = context;

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 14));


        glTextureManager = new GLTextureManager(context);
        glShaderManager = new GLShaderManager();

    }

    float offset = 0.001f;

    long lastTime = 0L;

    float[] mvp = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        if(particleSystemList.size() > 0) {
            for (ParticleSystem particleSystem : particleSystemList) {
                if (particleSystem != null) {
                    Matrix.setIdentityM(mvp, 0);

                    //偏移
                    //            PointF pointF = aMap.getProjection().toOpenGLLocation(center);
                    //
                    //            Matrix.multiplyMM(mvp,0, aMap.getProjectionMatrix(),0,aMap.getViewMatrix(),0);
                    //
                    //            Matrix.translateM(mvp, 0 , pointF.x , pointF.y  , 0);
                    //            int scale = 10000;
                    //            Matrix.scaleM(mvp, 0 , scale, scale, scale);

                    Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

                    particleSystem.draw(mMVPMatrix);
                }
                //        long curTime = System.currentTimeMillis();
                //        if(curTime - lastTime > 16) {
                //
                //
                //            lastTime = curTime;
                //
                //            //来回移动
                ////            offset = -offset;
                //            center = new LatLng(center.latitude + offset,center.longitude);
                //
                //            //重新计算偏移位置
                //            calScaleAndTranslate();
                //
                //            aMap.moveCamera(CameraUpdateFactory.changeLatLng(center));
                //        }
            }
        }
    }

    List<ParticleSystem> particleSystemList = new ArrayList<ParticleSystem>();


    float ratio = 1;
    int width = 0;
    int height = 0;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        ratio = (float) width / height;
        // create a projection matrix from device screen geometry
//        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.orthoM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


//        particleSystemList.add(generateSnowParticle());
        particleSystemList.add(generateRainParticle());
    }

    private ParticleSystem generateSnowParticle() {
        ParticleSystem particleSystem = new ParticleSystem();
        particleSystem.setShownSize(width,height);
        particleSystem.setgLShaderManager(glShaderManager);
        particleSystem.setGlTextureManager(glTextureManager);

        particleSystem.setMaxParticles(1000);
        particleSystem.setDuration(5000);
        particleSystem.setParticleEmission(new ParticleEmissonModule(20, 1000));
        particleSystem.setLoop(true);
        particleSystem.setParticleShapeModule(new RectParticleShape(-ratio,1, ratio,0.8f));


        // snow
        particleSystem.setPreWraw(true);
        particleSystem.setParticleLifeTime(10000);
        particleSystem.setParticleStartSpeed(1);
        ParticleOverLifeModule particleOverLifeModule = new ParticleOverLifeModule();
        //snow (random.nextFloat() * 0.1f, -(random.nextFloat() * 0.1f + 0.1f), 0);
        particleOverLifeModule.setVelocityOverLife(new RandomVelocityBetweenTwoConstants(-0.2f, -0.1f,0,0.2f,-0.2f,0));
        particleSystem.setParticleOverLifeModule(particleOverLifeModule);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.snow);
        particleSystem.setTexture(bitmapDescriptor);
        return particleSystem;
    }
    private ParticleSystem generateRainParticle() {
        ParticleSystem particleSystem = new ParticleSystem();
        particleSystem.setShownSize(width,height);
        particleSystem.setgLShaderManager(glShaderManager);
        particleSystem.setGlTextureManager(glTextureManager);
        particleSystem.setMaxParticles(1000);
        particleSystem.setDuration(5000);
        particleSystem.setParticleEmission(new ParticleEmissonModule(100, 1000));
        particleSystem.setLoop(true);
        particleSystem.setParticleShapeModule(new RectParticleShape(-ratio,1, ratio,0.8f));
        // rain
        particleSystem.setPreWraw(true);
        particleSystem.setParticleLifeTime(5000);
        particleSystem.setParticleStartSpeed(1);
        ParticleOverLifeModule particleOverLifeModule = new ParticleOverLifeModule();
        //rain -0.1f, (-0.5,0.5), 0
        particleOverLifeModule.setVelocityOverLife(new RandomVelocityBetweenTwoConstants(-0.1f, -2f,0,-0.1f,-1f,0));
        particleSystem.setParticleOverLifeModule(particleOverLifeModule);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.rain);
        particleSystem.setTexture(bitmapDescriptor);
        return particleSystem;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void OnMapReferencechanged() {

    }


    public void onDestroy() {
        aMap = null;
        context = null;

        for(ParticleSystem particleSystem : particleSystemList) {
            if (particleSystem != null) {
                particleSystem.destroy();
            }
            particleSystem = null;
        }
        particleSystemList.clear();
    }
}
