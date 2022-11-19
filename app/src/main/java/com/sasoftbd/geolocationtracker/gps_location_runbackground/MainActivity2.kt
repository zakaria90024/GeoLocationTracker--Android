package com.sasoftbd.geolocationtracker.gps_location_runbackground

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.Surface
import android.view.View
import androidx.activity.compose.setContent
import androidx.camera.core.Preview
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.provider.FontsContractCompat.Columns
import com.sasoftbd.geolocationtracker.R
import java.util.jar.Manifest

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )

    }

    fun WhenStopClicked(view: View) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            stopService(this)
        }
    }

    fun WhenStartClicked(view: View) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }


}
