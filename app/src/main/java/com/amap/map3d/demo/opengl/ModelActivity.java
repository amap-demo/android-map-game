package com.amap.map3d.demo.opengl;

import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.MapView;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.opengl.objmodel.MapRenderer;

/**
 * AMap地图中介绍如何使用OPengl
 */
public class ModelActivity extends android.app.Activity  {

    private MapView mapView;
    private AMap aMap;

    CustomRenderer renderer = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengl_activity);
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
            renderer = new MapRenderer(aMap,this);

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
