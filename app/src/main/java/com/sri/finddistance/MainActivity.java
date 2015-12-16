package com.sri.finddistance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.sri.finddistance.gson.Directions;
import com.sri.finddistance.gson.GeoCode;
import com.sri.finddistance.utils.L;
import com.sri.finddistance.utils.MapUtils;
import com.sri.finddistance.utils.ServerUtils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseMapActivity {

    private GoogleMap map;

    private TextView srcLocation;
    private TextView destLocation;
    private TextView distanceTextView;
    private LinearLayout distanceLayout;

    private LatLng srcLatLng;
    private LatLng destLatLng;

    private GeoCode srcGeoCode;
    private GeoCode destGeoCode;
    private Directions directionsObj;

    private Marker fromMarker;
    private Marker toMarker;

    private OkHttpClient okHttpClient = new OkHttpClient();

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initComponents();
    }

    private void initUI() {
        srcLocation = (TextView) findViewById(R.id.srcLocation);
        destLocation = (TextView) findViewById(R.id.destLocation);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);

        distanceLayout = (LinearLayout) findViewById(R.id.distanceLayout);
        distanceLayout.setVisibility(View.GONE);
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

        resetMap();
    }

    private void resetMap() {
        //Focus Bangalore
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtils.BANGALORE_LATLNG, 10f));
    }

    private void resetAll() {
        resetMap();
        distanceLayout.setVisibility(View.GONE);
        srcLocation.setText("");
        destLocation.setText("");
        srcLatLng = null;
        destLatLng = null;
        srcGeoCode = null;
        destGeoCode = null;
        directionsObj = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            LatLng latlng = data.getParcelableExtra(MapUtils.SELECTED_LATLNG);
            if (requestCode == MapUtils.SELECT_SRC) {
                srcLatLng = latlng;
            } else {
                destLatLng = latlng;
            }
            findGeoCode(latlng, requestCode);
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.Base_Theme_AppCompat_Light_Dialog);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Fetching Location");
        }

        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void srcLocationClicked(View v) {
        Intent i = new Intent(MainActivity.this, SelectPinPointActivity.class);
        if (srcLatLng != null) {
            i.putExtra("LATLNG", srcLatLng);
        }
        startActivityForResult(i, MapUtils.SELECT_SRC);
    }

    public void destLocationClicked(View v) {
        Intent i = new Intent(MainActivity.this, SelectPinPointActivity.class);
        if (destLatLng != null) {
            i.putExtra("LATLNG", destLatLng);
        }
        startActivityForResult(i, MapUtils.SELECT_DEST);
    }

    public void getDistanceClicked(View v) {
        if (srcLatLng != null && destLatLng != null) {
            findDirection();
        }
    }

    private void findDirection() {
        distanceLayout.setVisibility(View.GONE);
        showProgressDialog();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("origin", srcLatLng.latitude + "," + srcLatLng.longitude);
        parameters.put("destination", destLatLng.latitude + "," + destLatLng.longitude);
        parameters.put("sensor", "false");

        ServerUtils.get(ServerUtils.GMAP_DIRECTIONS_URL, parameters, new ServerUtils.ServerListener() {
            @Override
            public void onComplete() {
                dismissProgressDialog();
            }
            @Override
            public void onSuccess(String response) {
                L.d("response: " + response);
                Gson gson = new Gson();
                directionsObj = gson.fromJson(response, Directions.class);
                if (directionsObj.status.equals("OK")) {
                    try {
                        updateDistance(directionsObj.getDistance());
                    } catch (Exception e) {
                        L.logException(e);
                    }
                }
            }
            @Override
            public void onFailure(String error) {
                L.e("error: " + error);
            }
        });
    }

    private void findGeoCode(final LatLng latlng, final int type) {
        resetMap();
        distanceLayout.setVisibility(View.GONE);
        showProgressDialog();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("latlng", latlng.latitude + "," + latlng.longitude);
        parameters.put("sensor", "false");

        ServerUtils.get(ServerUtils.GMAP_GEOCODE_URL, parameters, new ServerUtils.ServerListener() {
            @Override
            public void onComplete() {
                dismissProgressDialog();
            }
            @Override
            public void onSuccess(String response) {
                L.d("response: " + response);
                Gson gson = new Gson();
                GeoCode geoCode = gson.fromJson(response, GeoCode.class);
                L.d("status code: " + geoCode.status);
                if (geoCode.status.equals("OK")) {
                    String formattedAddress = geoCode.results.get(0).formatted_address;
                    if (type == MapUtils.SELECT_SRC) {
                        srcGeoCode = geoCode;
                    } else {
                        destGeoCode = geoCode;
                    }
                } else {
                    if (type == MapUtils.SELECT_SRC) {
                        srcGeoCode = null;
                    } else {
                        destGeoCode = null;
                    }
                }
                updateLocation();
            }
            @Override
            public void onFailure(String error) {
                L.e("error: " + error);
            }
        });
    }

    private void updateLocation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                srcLocation.setText("");
                destLocation.setText("");
                if (srcGeoCode != null) {
                    srcLocation.setText(srcGeoCode.results.get(0).formatted_address);
                } else if (srcLatLng != null) {
                    srcLocation.setText(srcLatLng.latitude + ", " + srcLatLng.longitude);
                }
                if (destGeoCode != null) {
                    destLocation.setText(destGeoCode.results.get(0).formatted_address);
                } else if (destLatLng != null) {
                    destLocation.setText(destLatLng.latitude + ", " + destLatLng.longitude);
                }
            }
        });
    }

    private void updateDistance(final String distance) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                distanceTextView.setText(distance.toUpperCase());
                distanceLayout.setVisibility(View.VISIBLE);

                //from marker
                MarkerOptions fromMarkerOptions = new MarkerOptions();
                fromMarkerOptions.position(srcLatLng);
                fromMarkerOptions.title("FROM");
                fromMarker = map.addMarker(fromMarkerOptions);
                fromMarker.showInfoWindow();

                //to marker
                MarkerOptions toMarkerOptions = new MarkerOptions();
                toMarkerOptions.position(destLatLng);
                toMarkerOptions.title("TO");
                toMarker = map.addMarker(toMarkerOptions);
                toMarker.showInfoWindow();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(srcLatLng);
                builder.include(destLatLng);
                LatLngBounds bounds = builder.build();

//                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, -50));
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150, 150, 10));
            }
        });
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        progressDialog = null;
        super.onDestroy();
    }
}
