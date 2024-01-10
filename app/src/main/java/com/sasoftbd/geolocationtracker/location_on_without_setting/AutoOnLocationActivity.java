package com.sasoftbd.geolocationtracker.location_on_without_setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
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

//        locationCallback = new LocationCallback() {
//
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                //updateUIValue(locationResult.getLastLocation());
//                Toast.makeText(AutoOnLocationActivity.this, "called", Toast.LENGTH_SHORT).show();
//            }
//        };


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
                forceGpsON();
            }
        });

        ActivityCompat.requestPermissions(AutoOnLocationActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PackageManager.PERMISSION_GRANTED);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);




//        updateGPS();
//
//        Handler handler = new Handler();
//
//// Post a Runnable to update the UI after a 2-second delay
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Update UI elements here
//                startLocationUpdate();
//            }
//        }, 5000); // Delay in milliseconds
//

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

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValue(locationResult.getLastLocation());
            }
        };


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
    private void updateUIValue(Location lastLocation) {

        Geocoder geocoder = new Geocoder(AutoOnLocationActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
            Toast.makeText(this, "Lat=" + lastLocation.getLatitude() + "\nLong=" + lastLocation.getLongitude() + "\nAddress=" + addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
        } catch (Exception e) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //it work for location get and
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 101:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        //Toast.makeText(AttendanceActivity.this,states.isLocationPresent()+"",Toast.LENGTH_SHORT).show();
                        updateGPS();
                        startLocationUpdate();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        // Toast.makeText(AttendanceActivity.this, "Canceled", Toast.LENGTH_SHORT).show();


                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        forceGpsON();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        ActivityCompat.finishAffinity(AutoOnLocationActivity.this);
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(AutoOnLocationActivity.this);
                        builder.setMessage("Location is Mandatory").setPositiveButton("Try Again", dialogClickListener)
                                .setNegativeButton("Exit", dialogClickListener).show();
                        break;
                    default:
                        break;
                }
                break;
        }


    }

    void forceGpsON() {

        LocationRequest locationRequest1 = LocationRequest.create();
        locationRequest1.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest1.setInterval(10000);
        locationRequest1.setFastestInterval(10000 / 2);
        LocationSettingsRequest.Builder locationSettingBuilder = new LocationSettingsRequest.Builder();
        locationSettingBuilder.addLocationRequest(locationRequest1);
        locationSettingBuilder.setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingBuilder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                updateGPS();

            }
        });


        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        AutoOnLocationActivity.this,
                                        101);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });



    }


}