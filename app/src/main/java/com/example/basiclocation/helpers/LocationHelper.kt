package com.example.basiclocation.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationHelper(private val activity: ComponentActivity) {
    private val TAG = "LocationHelper"
    private val REQUEST_CHECK_SETTINGS = 0x1

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    private var locationCallback: LocationCallback
    private var locationRequest: LocationRequest

    private var onLocationUpdateListener: ((Location) -> Unit)? = null
    private var locationPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

    init {

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(TAG, "Location update: ${location.latitude}, ${location.longitude}")
                    onLocationUpdateListener?.invoke(location)
                }
            }
        }
    }

    fun setupLocationPermissionRequest(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        locationPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.d(TAG, "Location permission granted")
                    onPermissionGranted()
                }
                else -> {
                    Log.e(TAG, "Location permission denied")
                    onPermissionDenied()
                }
            }
        }
    }

    fun requestLocationPermissions() {
        locationPermissionLauncher?.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Location settings enabled")
            } else {
                Log.e(TAG, "Location settings NOT enabled")
                Toast.makeText(activity, "Activez la localisation pour utiliser l'appli", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(onLocationReceived: (Location?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                Log.d(TAG, "Last location: $location")
                onLocationReceived(location)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting last location", e)
                onLocationReceived(null)
            }
    }

    fun setLocationUpdateListener(listener: (Location) -> Unit) {
        onLocationUpdateListener = listener
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        Log.d(TAG, "Location updates started")
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "Location updates stopped")
    }
}