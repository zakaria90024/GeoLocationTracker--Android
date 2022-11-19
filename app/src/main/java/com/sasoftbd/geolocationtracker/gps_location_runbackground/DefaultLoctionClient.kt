package com.sasoftbd.geolocationtracker.gps_location_runbackground

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLoctionClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
//            if (context.hasLocationPermission()) {
//                throw LocationClient.LocationException("Missing Location Permission")
//            }
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNatworkEnable =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnable && !isNatworkEnable) {
                throw LocationClient.LocationException("Gps is disable")
            }

            val request =
                com.google.android.gms.location.LocationRequest.create().setInterval(interval)
                    .setFastestInterval(interval)
            val locationCallback = object : LocationCallback() {

                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    p0.locations.lastOrNull()?.let { location ->
                        launch {
                            send(location)
                        }
                    }
                }
            }

            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}