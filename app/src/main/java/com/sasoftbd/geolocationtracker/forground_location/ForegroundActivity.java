package com.sasoftbd.geolocationtracker.forground_location;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sasoftbd.geolocationtracker.R;
//
//public class ForegroundActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_foreground);
//    }
//}


import android.Manifest;

import android.app.AlertDialog;

import android.content.Context;

import android.content.DialogInterface;

import android.content.Intent;

import android.content.IntentSender;

import android.location.LocationManager;

import android.net.Uri;

import android.os.Bundle;

import android.provider.Settings;

import android.view.View;

import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationSettingsRequest;

import com.google.android.gms.location.LocationSettingsResponse;

import com.karumi.dexter.Dexter;

import com.karumi.dexter.MultiplePermissionsReport;

import com.karumi.dexter.PermissionToken;

import com.karumi.dexter.listener.PermissionRequest;

import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


public class ForegroundActivity extends AppCompatActivity {

    private Button btn_get;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_foreground);


        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        btn_get = findViewById(R.id.btn_get);

        btn_get.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    enableLocationSettings();

                } else {

                    requestAppPermissions();

                }

            }

        });

    }

    private void requestAppPermissions() {

        Dexter.withActivity(ForegroundActivity.this)

                .withPermissions(

                        Manifest.permission.ACCESS_FINE_LOCATION,

                        Manifest.permission.ACCESS_COARSE_LOCATION)

                .withListener(new MultiplePermissionsListener() {

                    @Override

                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        // check if all permissions are granted

                        if (report.areAllPermissionsGranted()) {

                            // do you work now

                            //interact.downloadImage(array);

                            startService(new Intent(ForegroundActivity.this, ForegroundService.class));

                        }


                        // check for permanent denial of any permission

                        if (report.isAnyPermissionPermanentlyDenied()) {

                            // permission is denied permanently, navigate user to app settings

                            showSettingsDialog();

                            //finish();

                        }

                    }

                    @Override

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }

                })

                .onSameThread()

                .check();

    }

    private void showSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ForegroundActivity.this);

        builder.setTitle("Need Permissions");

        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");

        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                openSettings();

            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }

        });

        builder.show();

    }

    private void openSettings() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        Uri uri = Uri.fromParts("package", getPackageName(), null);

        intent.setData(uri);

        startActivityForResult(intent, 101);

    }

    protected void enableLocationSettings() {

        LocationRequest locationRequest = LocationRequest.create()

                .setInterval(1000)

                .setFastestInterval(3000)

                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()

                .addLocationRequest(locationRequest);

        LocationServices

                .getSettingsClient(this)

                .checkLocationSettings(builder.build())

                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {

                    // startUpdatingLocation(...);

                })

                .addOnFailureListener(this, ex -> {

                    if (ex instanceof ResolvableApiException) {

                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.

                        try {

                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().

                            ResolvableApiException resolvable = (ResolvableApiException) ex;

                            resolvable.startResolutionForResult(this, 123);

                        } catch (IntentSender.SendIntentException sendEx) {

                            // Ignore the error.

                        }

                    }

                });

    }

}