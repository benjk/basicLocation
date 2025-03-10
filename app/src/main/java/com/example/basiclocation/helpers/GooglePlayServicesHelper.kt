package com.example.basiclocation.helpers

import android.content.IntentSender
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient

class GooglePlayServicesHelper(private val activity: ComponentActivity) {
    private val TAG = "GooglePlayServicesHelper"
    val REQUEST_CHECK_SETTINGS = 0x1

    fun isGooglePlayServicesAvailable(): Boolean {
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity) == ConnectionResult.SUCCESS
        if (!available) {
            Log.e(TAG, "Google Play Services not available")
        }
        return available
    }

    fun checkLocationSettings(
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Log.d(TAG, "Location settings are satisfied")
            onSuccess()
        }

        task.addOnFailureListener { exception ->
            Log.e(TAG, "Location settings are not satisfied")
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e(TAG, "Failed to show settings dialog", sendEx)
                    onFailure(sendEx)
                }
            } else {
                onFailure(exception)
            }
        }
    }
}