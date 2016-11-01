package com.amap.map3d.demo.opengl;

import android.os.Bundle;
import android.widget.SeekBar;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.opengl.mask.MaskMapRender;
import com.amap.map3d.demo.opengl.particle.ParticleMapRender;

import java.util.ArrayList;
import java.util.Random;

/**
 * AMap地图中介绍如何使用OPengl
 */
public class ParticleActivity extends android.app.Activity  {

    private MapView mapView;
    private AMap aMap;

    CustomRenderer renderer = null;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengl_activity);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

    }

    /**
     * 初始化AMap对象
     */
    private void init() {


        if (aMap == null) {
            aMap = mapView.getMap();

            //不显示文字
            aMap.showMapText(false);
            //不显示建筑物
            aMap.showBuildings(false);

            //初始化renderer
            renderer = new ParticleMapRender(aMap, this);

            //设置renderer
            aMap.setCustomRenderer(renderer);
        }
    }


    private boolean running = false;
    private int period = 1000/85;
    /**
     * Maximum gameloops that can happen without a sleep or yield
     */
    public static final int MAX_CONSECUTIVE_LOOPS = 17;

    /**
     * Maximium number of gameUpdates that can occur without gameRenders
     */
    private static final int MAX_SKIP_FRAMES = 5;


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        running = true;

        new Thread(new Runnable() {
            public void run() {
                running = true;
                long beforeTime, afterTime, elapsed, timeToSleep;
                long overSleepTime = 0;
                long excess = 0;
                int noDelays = 0;

                beforeTime = getMillis();
                //lastStatsTime = beforeTime;
                while (running) {

//                    mGLView.requestRender(); //请求刷新
                    if(aMap != null) {
                        aMap.runOnDrawFrame();
                    }

                    afterTime = getMillis();
                    elapsed = afterTime - beforeTime; //how long did we take to hander?
                    timeToSleep = (period - elapsed) - overSleepTime; //sleep excess time if we were too fase
                    if (timeToSleep > 0) {
                        try {
                            Thread.sleep(timeToSleep);
                        } catch(InterruptedException ex) {
                        }
                        //did we sleep too much? deduct from next loop
                        overSleepTime = (getMillis() - afterTime) - timeToSleep;
                    } else {
                        excess -= timeToSleep;//how much did we miss the target?
                        //rendering took more than it should! run next loop right away
                        overSleepTime = 0;
                        if (++noDelays >= MAX_CONSECUTIVE_LOOPS) { //consective loops. Better give other thrads a chance to run!
                            Thread.yield();
                            noDelays = 0;
                        }

                    }
                    beforeTime = getMillis();

                    //rendering took to much time! Skip some rendering frames to achieve desired FPU
                    int gameUpdatesWithoutRender = 0;
                    while (excess > period && gameUpdatesWithoutRender < MAX_SKIP_FRAMES) {

                        excess -= period;
                        gameUpdatesWithoutRender++;
                    }
                }
            }
        }).start();
    }

    public static long getMillis() {
        return System.nanoTime() / 1000000;
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

        running = false;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
