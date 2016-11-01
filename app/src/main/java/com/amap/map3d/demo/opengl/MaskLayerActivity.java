package com.amap.map3d.demo.opengl;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.MapView;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.opengl.mask.MaskMapRender;

/**
 * AMap地图中介绍如何使用OPengl
 */
public class MaskLayerActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private MapView mapView;
    private AMap aMap;


    //变化颜色用的seekbar
    private SeekBar mRBar;
    private SeekBar mGBar;
    private SeekBar mBBar;
    private SeekBar mABar;

    CustomRenderer renderer = null;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengl_activity_with_seek);
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


        mRBar = (SeekBar) findViewById(R.id.rSeekBar);
        mRBar.setMax(255);
        mRBar.setProgress(40);

        mGBar = (SeekBar) findViewById(R.id.gSeekBar);
        mGBar.setMax(255);
        mGBar.setProgress(100);

        mBBar = (SeekBar) findViewById(R.id.bSeekBar);
        mBBar.setMax(255);
        mBBar.setProgress(125);

        mABar = (SeekBar) findViewById(R.id.bSeekBar);
        mABar.setMax(255);
        mABar.setProgress(125);

        mRBar.setOnSeekBarChangeListener(this);
        mGBar.setOnSeekBarChangeListener(this);
        mBBar.setOnSeekBarChangeListener(this);
        mABar.setOnSeekBarChangeListener(this);

        if (aMap == null) {
            aMap = mapView.getMap();

            //不显示文字
            aMap.showMapText(false);
            //不显示建筑物
            aMap.showBuildings(false);

            //设置夜间模式
            aMap.setMapType(AMap.MAP_TYPE_NIGHT);

            //初始化renderer
            renderer = new MaskMapRender(aMap);

            //设置renderer
            aMap.setCustomRenderer(renderer);

        }
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


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        // 修改蒙版颜色
        if(renderer instanceof  MaskMapRender) {
            int r = mRBar.getProgress();
            int g = mGBar.getProgress();
            int b = mBBar.getProgress();
            int a = mABar.getProgress();

            ((MaskMapRender) renderer).setRGBA(r,g,b,a);
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
