package com.amap.map3d.demo;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amap.api.maps.MapsInitializer;
import com.amap.map3d.demo.opengl.CubeActivity;
import com.amap.map3d.demo.opengl.MS3DModelActivity;
import com.amap.map3d.demo.opengl.MaskLayerActivity;
import com.amap.map3d.demo.opengl.ModelActivity;
import com.amap.map3d.demo.opengl.ParticleActivity;
import com.amap.map3d.demo.view.FeatureView;

/**
 * AMapV2地图demo总汇
 */
public final class MainActivity extends ListActivity {
    private static class DemoDetails {
        private final int titleId;
        private final int descriptionId;
        private final Class<? extends android.app.Activity> activityClass;

        public DemoDetails(int titleId, int descriptionId,
                           Class<? extends android.app.Activity> activityClass) {
            super();
            this.titleId = titleId;
            this.descriptionId = descriptionId;
            this.activityClass = activityClass;
        }
    }

    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }
            DemoDetails demo = getItem(position);
            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);
            return featureView;
        }
    }

    private static final DemoDetails[] demos = {
            new DemoDetails(R.string.opengl_demo, R.string.opengl_description_cube,
                    CubeActivity.class),
            new DemoDetails(R.string.opengl_demo, R.string.opengl_description_mask,
                    MaskLayerActivity.class),
            new DemoDetails(R.string.opengl_demo, R.string.opengl_description_particle,
                    ParticleActivity.class),
            new DemoDetails(R.string.opengl_demo, R.string.opengl_description_model,
                    ModelActivity.class),
            new DemoDetails(R.string.opengl_demo, R.string.opengl_description_model_ms3d,
                    MS3DModelActivity.class),

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setTitle("3D地图Demo" + MapsInitializer.getVersion());
        ListAdapter adapter = new CustomArrayAdapter(
                this.getApplicationContext(), demos);
        setListAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DemoDetails demo = (DemoDetails) getListAdapter().getItem(position);
        startActivity(new Intent(this.getApplicationContext(),
                demo.activityClass));
    }
}
