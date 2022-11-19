package com.sasoftbd.geolocationtracker.freecodecamp_gps_app_video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sasoftbd.geolocationtracker.R;
import com.sasoftbd.geolocationtracker.gps_location_runbackground.MainActivity2;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //each ui map
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_update = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationUpdate = findViewById(R.id.sw_locationsupdates);
        btn_newWayPoint = findViewById(R.id.btn_waypoint);
        btn_showWayPoing = findViewById(R.id.btn_waypoint_list);
        tv_waypointCounts = findViewById(R.id.tv_waypoint);
        btn_showInMap = findViewById(R.id.btn_Showmap);
        btn_ClearData = findViewById(R.id.btn_ClearData);


        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValue(locationResult.getLastLocation());
            }
        };

        btn_showInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(in);
            }
        });

        btn_newWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the gps location
                //add the new location
                MyApplication myApplication = (MyApplication) getApplicationContext();
                savedLocations = myApplication.getmylocations();
                savedLocations.add(currentLocation);
            }
        });
        btn_showWayPoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inn = new Intent(MainActivity.this, ShowSavedLocations.class);
                startActivity(inn);
            }
        });

        btn_ClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyApplication myApplication = (MyApplication) getApplicationContext();
                savedLocations = myApplication.getmylocations();
                savedLocations.clear();
                tv_waypointCounts.setText("0");
                Toast.makeText(myApplication, "Clear Successful!", Toast.LENGTH_SHORT).show();
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensor");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers +  WIFI");
                }

            }
        });

        sw_locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationUpdate.isChecked()) {
                    //turn on location tracking
                    startLocationUpdate();
                } else {
                    stopLocationUpdate();
                }
            }
        });


        updateGPS();

    }

    private void startLocationUpdate() {
        tv_update.setText("Location is being tracked");
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


    private void stopLocationUpdate() {

        tv_update.setText("Location is Not being tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    private void updateGPS() {
        //get gps permission
        //get current location fused client
        //update ui
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValue(location);
                    currentLocation = location;
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }

        }
    }

    private void updateUIValue(Location location) {

        try {
            tv_lat.setText(String.valueOf(location.getLatitude()));
            tv_lon.setText(String.valueOf(location.getLongitude()));
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));

            if (location.hasAltitude()) {
                tv_altitude.setText(String.valueOf(location.getAltitude()));
            } else {
                tv_altitude.setText("Not available");
            }

            if (location.hasSpeed()) {
                tv_speed.setText(String.valueOf(location.getSpeed()));
            } else {
                tv_altitude.setText("Not available");
            }
            Geocoder geocoder = new Geocoder(MainActivity.this);
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                tv_address.setText(addresses.get(0).getAddressLine(0));
            } catch (Exception e) {
                tv_address.setText("Unable to get Street Address");
            }

            MyApplication myApplication = (MyApplication) getApplicationContext();
            savedLocations = myApplication.getmylocations();


            //saved previous location
            tv_waypointCounts.setText(Integer.toString(savedLocations.size()));


        } catch (Exception e) {
            Toast.makeText(this, "Your Phone Location Off! Please Turn On", Toast.LENGTH_SHORT).show();
//            final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void WhenClickFourgroundClick(View view) {
        Intent in = new Intent(this, MainActivity2.class);
        startActivity(in);
    }
}