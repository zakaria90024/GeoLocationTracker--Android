package com.sasoftbd.geolocationtracker;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    public static MyApplication singleton;
    private List<Location> myLocations;

    public List<Location> getmylocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public MyApplication getInstance() {
        return singleton;
    }

    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
    }
}
