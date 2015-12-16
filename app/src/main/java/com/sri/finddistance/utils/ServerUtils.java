package com.sri.finddistance.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by sridhar on 15/12/15.
 */
public class ServerUtils {

    public static final String GMAP_DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/json?";
    public static final String GMAP_GEOCODE_URL = "http://maps.googleapis.com/maps/api/geocode/json?";

    private static OkHttpClient okHttpClient = null;

    private ServerUtils() {

    }

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
            //timeout 10 seconds
            okHttpClient.setConnectTimeout(10 * 1000, TimeUnit.MILLISECONDS);
        }

        return okHttpClient;
    }

    public static void get(String url, Map<String, String> parameters, final ServerListener listener) {
        for (String param : parameters.keySet()) {
            url += param + "=" + parameters.get(param) + "&";
        }
        final String paramUrl = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    L.d("Trying to get URL: " + paramUrl);
                    Request request = new Request.Builder().url(paramUrl).build();
                    Response response = getInstance().newCall(request).execute();
                    listener.onComplete();
                    listener.onSuccess(response.body().string());
                } catch (Exception e) {
                    L.logException(e);
                    listener.onComplete();
                    listener.onFailure(e.getMessage());
                }
            }
        }).start();
    }

    public interface ServerListener {
        public void onComplete();
        public void onSuccess(String response);
        public void onFailure(String error);
    }
}
