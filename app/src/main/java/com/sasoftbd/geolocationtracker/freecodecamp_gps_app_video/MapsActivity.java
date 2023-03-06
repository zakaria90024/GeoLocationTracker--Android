package com.sasoftbd.geolocationtracker.freecodecamp_gps_app_video;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sasoftbd.geolocationtracker.R;
import com.sasoftbd.geolocationtracker.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    List<Location> savedLocations;
    LatLng latLng;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MyApplication myApplication = (MyApplication) getApplicationContext();
        savedLocations = myApplication.getmylocations();


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng lastLocationPlaced = sydney;

        for (Location location : savedLocations) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = mMap.addMarker( new MarkerOptions()
                    .position(latLng).title("London").snippet("Person"+savedLocations)
            );
            marker.showInfoWindow();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Lat:" + location.getLatitude() + "Long:" + location.getLongitude());
            mMap.addMarker(markerOptions);
            //for pin click show lat long
            lastLocationPlaced = latLng;


        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                //lets count the number the pin is clicked

                Integer clicks = (Integer) marker.getTag();
                if (clicks == null) {
                    clicks = 0;
                }
                clicks++;
                marker.setTag(clicks);

//                Marker markers = mMap.addMarker( new MarkerOptions()
//
//                        .position(latLng).title(marker.getTitle()).snippet("Person"+savedLocations)
//                );
//                markers.showInfoWindow();

                Toast.makeText(MapsActivity.this, "Title:" + marker.getTitle() + "Tag" + marker.getTag(), Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }
}