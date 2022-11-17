package com.sasoftbd.geolocationtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ShowSavedLocations extends AppCompatActivity {

    ListView lv_savedLoctions;
    List<Location> savedLocations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_locations);


        MyApplication myApplication = (MyApplication) getApplicationContext();
        savedLocations = myApplication.getmylocations();

//        MyApplication myApplication = (MyApplication) getApplicationContext();
//        List<Location> savedLocations = myApplication.getmylocations();

        lv_savedLoctions.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1,savedLocations));
    }
}