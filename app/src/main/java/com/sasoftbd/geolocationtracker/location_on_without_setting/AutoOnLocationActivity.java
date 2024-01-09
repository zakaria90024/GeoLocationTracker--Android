package com.sasoftbd.geolocationtracker.location_on_without_setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sasoftbd.geolocationtracker.R;
import com.sasoftbd.geolocationtracker.freecodecamp_gps_app_video.MainActivity;

import java.util.List;

public class AutoOnLocationActivity extends AppCompatActivity {


    private static final int FAST_UPDATE_INTERVAL = 30;
    private static final int DEFAULT_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_update, tv_address, tv_waypointCounts;

    Switch sw_locationUpdate, sw_gps;
    AppCompatButton btn_newWayPoint, btn_showWayPoing, btn_showInMap, btn_ClearData;

    boolean updateOn = false;
    //current location
    Location currentLocation;
    //list of saved locations
    List<Location> savedLocations;


    //google location tracking api
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;
    LocationCallback locationCallback;


    Button btn_CheckStatus;
    Button btn_OnGPS;
    TextView textViewTv;

    private LocationManager locationManager;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_on_location);



        locationRequest = new LocationRequest();
        locationRequest.setInterval(100 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(100 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //updateUIValue(locationResult.getLastLocation());
                Toast.makeText(AutoOnLocationActivity.this, "called", Toast.LENGTH_SHORT).show();
            }
        };


        textViewTv = findViewById(R.id.tv);
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


        updateGPS();

        Handler handler = new Handler();

// Post a Runnable to update the UI after a 2-second delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update UI elements here
                startLocationUpdate();
            }
        }, 5000); // Delay in milliseconds


    }


    private void startLocationUpdate() {
        //tv_update.setText("Location is being tracked");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }

    public void buttonCheckGps_Status(View view) {


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            textViewTv.setText("GPS is ON");
        } else {
            textViewTv.setText("GPS is OFF");
        }


    }


    private void updateGPS() {
        //get gps permission
        //get current location fused client
        //update ui
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AutoOnLocationActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Toast.makeText(AutoOnLocationActivity.this, "location"+location, Toast.LENGTH_SHORT).show();
                    //updateUIValue(location);
                    currentLocation = location;
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }

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

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(AutoOnLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AutoOnLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Toast.makeText(AutoOnLocationActivity.this, "dfds"+location, Toast.LENGTH_SHORT).show();

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Use latitude and longitude as needed

                    Toast.makeText(AutoOnLocationActivity.this, "lat="+latitude+"\nlat"+longitude, Toast.LENGTH_SHORT).show();
                }


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