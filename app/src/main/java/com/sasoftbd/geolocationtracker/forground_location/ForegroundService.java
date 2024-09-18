package com.sasoftbd.geolocationtracker.forground_location;

import android.Manifest;

import android.app.NotificationChannel;

import android.app.NotificationManager;

import android.app.PendingIntent;

import android.app.Service;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.os.Binder;

import android.os.Build;

import android.os.IBinder;

import android.widget.Toast;



import androidx.core.app.NotificationCompat;

import androidx.core.content.ContextCompat;



import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationCallback;

import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationResult;

import com.google.android.gms.location.LocationServices;
import com.sasoftbd.geolocationtracker.R;
import com.sasoftbd.geolocationtracker.freecodecamp_gps_app_video.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ForegroundService extends Service {



    private final IBinder mBinder = new MyBinder();
    private static final String CHANNEL_ID = "2";
    public static final  int INTERVAL_TIME = 120000/2;//TWO MIN,  //600000; //10 MINUIT'S




    @Override

    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }



    @Override
    public void onCreate() {

        super.onCreate();
        buildNotification();
        requestLocationUpdates();

    }


    private void buildNotification() {

        //String stop = "stop";

        Intent notificationIntent = new Intent(this, MainActivity.class);  // TargetActivity is the Activity to open
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentTitle(getString(R.string.app_name))
                .setContentText("Location tracking is working")
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setDescription("Location tracking is working");
            channel.setSound(null, null);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

        }

        startForeground(1, builder.build());

    }

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();

        request.setInterval(INTERVAL_TIME);

        request.setFastestInterval(3000); //PHONE MOVEMENT TIME

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, new LocationCallback() {

                @Override

                public void onLocationResult(LocationResult locationResult) {


                    String location = "Latitude : " + locationResult.getLastLocation().getLatitude() +

                            "\nLongitude : " + locationResult.getLastLocation().getLongitude();

                    double latitude =locationResult.getLastLocation().getLatitude();  // Replace with your latitude
                    double longitude = locationResult.getLastLocation().getLongitude();

                    String fullAddress = getCompleteAddressString(latitude, longitude);
                    if (fullAddress != null) {
                        System.out.println("Full Address: " + fullAddress);
                    } else {
                        System.out.println("Address not found!");
                    }
                    Toast.makeText(ForegroundService.this, location+"\n"+fullAddress, Toast.LENGTH_SHORT).show();

                }

            }, null);

        } else {

            stopSelf();

        }

    }

    public String getCompleteAddressString(double latitude, double longitude) {
        StringBuilder fullAddress = new StringBuilder();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Combine different parts of the address
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    fullAddress.append(address.getAddressLine(i)).append(" ");
                }
            } else {
                return "No address found for the provided location.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unable to get address.";
        }

        return fullAddress.toString().trim();
    }

    public class MyBinder extends Binder {

        public ForegroundService getService() {

            return ForegroundService.this;

        }

    }

}

