package com.example.basiclocation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basiclocation.helpers.GooglePlayServicesHelper
import com.example.basiclocation.helpers.LocationHelper
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.ui.theme.MapComponent
import com.example.basiclocation.ui.theme.PointOfInterestCardDetail
import com.example.basiclocation.viewmodels.MapViewModel

class MapActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val REQUEST_CHECK_SETTINGS = 0x1

    private lateinit var locationHelper: LocationHelper
    private lateinit var googlePlayServicesHelper: GooglePlayServicesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize helpers
        locationHelper = LocationHelper(this)
        googlePlayServicesHelper = GooglePlayServicesHelper(this)

        // Setup location permission callback
        locationHelper.setupLocationPermissionRequest(
            onPermissionGranted = {
                checkGooglePlayAndLocationSettings()
            },
            onPermissionDenied = {
                Toast.makeText(this, "Autorisez la localisation, pour utiliser l'appli", Toast.LENGTH_LONG).show()
            }
        )

        setContent {
            // Create ViewModel
            val mapViewModel: MapViewModel = viewModel(
                factory = MapViewModel.Factory(locationHelper)
            )

            var selectedPOI by remember { mutableStateOf<PointOfInterest?>(null) }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Map Component
                    MapComponent(
                        viewModel = mapViewModel,
                        onPointOfInterestClicked = { poi ->
                            selectedPOI = poi
                        }
                    )

                    // Show POI detail dialog when selected
                    selectedPOI?.let { poi ->
                        PointOfInterestCardDetail(
                            pointOfInterest = poi,
                            onDismiss = {
                                selectedPOI = null
                                // Clear from viewModel if it was triggered by proximity
                                mapViewModel.clearNearbyPointOfInterest()
                            }
                        )
                    }
                }
            }
        }

        // Request location permissions
        locationHelper.requestLocationPermissions()
    }

    private fun checkGooglePlayAndLocationSettings() {
        if (!googlePlayServicesHelper.isGooglePlayServicesAvailable()) {
            Toast.makeText(this, "Installez Google Play, pour utiliser l'appli", Toast.LENGTH_LONG).show()
            return
        }

        googlePlayServicesHelper.checkLocationSettings(
            onSuccess = {
                // Location settings satisfied, nothing to do - the ViewModel will handle updates
            },
            onFailure = { exception ->
                // The googlePlayServicesHelper will show the dialog to enable location
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                // Location settings enabled by user
                // The ViewModel will handle location updates automatically
            } else {
                Toast.makeText(this, "Activez la localisation, pour utiliser l'appli", Toast.LENGTH_LONG).show()
            }
        }
    }
}