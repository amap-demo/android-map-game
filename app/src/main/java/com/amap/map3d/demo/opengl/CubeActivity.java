package com.amap.map3d.demo.opengl;

import android.os.Bundle;
import android.widget.SeekBar;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.opengl.cube.CubeMapRender;
import com.amap.map3d.demo.opengl.mask.MaskMapRender;
import com.amap.map3d.demo.opengl.particle.ParticleMapRender;

import java.util.ArrayList;
import java.util.Random;

/**
 * AMap地图中介绍如何使用OPengl
 */
public class CubeActivity extends android.app.Activity  {

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
            renderer = new CubeMapRender(aMap);

            //设置renderer
            aMap.setCustomRenderer(renderer);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

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
