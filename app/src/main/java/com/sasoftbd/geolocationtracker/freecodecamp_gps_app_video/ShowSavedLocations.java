package com.sasoftbd.geolocationtracker.freecodecamp_gps_app_video;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sasoftbd.geolocationtracker.R;

import java.util.List;

public class ShowSavedLocations extends AppCompatActivity {

    ListView lv_savedLoctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_locations);
        lv_savedLoctions = findViewById(R.id.lv_wayPoints);


        MyApplication myApplication = (MyApplication) getApplicationContext();
        List<Location> savedLocations = myApplication.getmylocations();

        if (savedLocations != null) {
            lv_savedLoctions.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, savedLocations));
        } else {
            Toast.makeText(myApplication, "Location List Empty", Toast.LENGTH_SHORT).show();
        }

    }
}