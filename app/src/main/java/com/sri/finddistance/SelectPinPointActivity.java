package com.sri.finddistance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.sri.finddistance.utils.L;
import com.sri.finddistance.utils.MapUtils;

public class SelectPinPointActivity extends BaseMapActivity {

    private GoogleMap map;

    private LatLng selectedLatLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pin_point);

        initUI();
        initComponents();
    }

    private void initUI() {
        getSupportActionBar().setSubtitle("Pan & zoom map under pin");
    }

    private void initComponents() {
        if (checkMapFeatureExists()) {
            MapFragment mapView = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
            map = mapView.getMap();

            initMap();
        } else {
            L.e("Map feature not exists");
            findViewById(R.id.mapView).setVisibility(View.GONE);
        }
    }

    private void initMap() {
        L.i("init map called");
        map.setMyLocationEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setTrafficEnabled(false);
        map.setBuildingsEnabled(true);
        LatLng latLng = MapUtils.BANGALORE_LATLNG;
        float zoom = 12f;
        if (getIntent().hasExtra("LATLNG")) {
            latLng = getIntent().getParcelableExtra("LATLNG");
            zoom = 15f;
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                selectedLatLng = cameraPosition.target;
            }
        });
    }

    public void okClicked(View v) {
//        Toast.makeText(this, "selected latlng: " + selectedLatLng.toString(), Toast.LENGTH_LONG).show();
        Intent data = new Intent();
        data.putExtra(MapUtils.SELECTED_LATLNG, selectedLatLng);
        setResult(RESULT_OK, data);
        finish();
    }

    public void cancelClicked(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

}
