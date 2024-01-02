package com.sasoftbd.geolocationtracker.location_on_without_setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sasoftbd.geolocationtracker.R;
import com.sasoftbd.geolocationtracker.freecodecamp_gps_app_video.MainActivity;

public class AutoOnLocationActivity extends AppCompatActivity {


    Button btn_CheckStatus;
    Button btn_OnGPS;
    TextView textViewTv;

    private LocationManager locationManager;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_on_location);


        TextView textViewTv = findViewById(R.id.tv);
        btn_CheckStatus = findViewById(R.id.btn_CheckStatus);
        btn_OnGPS = findViewById(R.id.btn_OnGPS);

        btn_CheckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonCheckGps_Status(view);
            }
        });

        btn_OnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSwitchGps_ON(view);
            }
        });

        ActivityCompat.requestPermissions(AutoOnLocationActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PackageManager.PERMISSION_GRANTED);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


    }

    public void buttonCheckGps_Status(View view) {


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            textViewTv.setText("GPS is ON");
        } else {
            textViewTv.setText("GPS is OFF");
        }


    }

    public void buttonSwitchGps_ON(View view) {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);
        LocationSettingsRequest.Builder locationSettingBuilder = new LocationSettingsRequest.Builder();
        locationSettingBuilder.addLocationRequest(locationRequest);
        locationSettingBuilder.setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingBuilder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                textViewTv.setText("Location Setting (GPS) is ON");
            }
        });


        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textViewTv.setText("Location Setting (GPS) is OFF");

                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;

                        resolvableApiException.startResolutionForResult(AutoOnLocationActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });


    }
}