package com.sri.finddistance;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by sridhar on 15/12/15.
 */
public class BaseMapActivity extends ActionBarActivity {

    protected boolean checkMapFeatureExists() {
        try {
            int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (statusCode != ConnectionResult.SUCCESS) {
                showMapErrorDialog(statusCode);
                return false;
            }
        } catch (Exception e) {
            showMapErrorDialog(0);
            return false;
        }

        return true;
    }

    private void showMapErrorDialog(int statusCode) {
        GooglePlayServicesUtil.getErrorDialog(statusCode, this, 0, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        }).show();
    }
}