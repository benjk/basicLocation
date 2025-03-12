package com.example.basiclocation

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
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
import com.example.basiclocation.helpers.PoiRepository
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.ui.comp.MapComponent
import com.example.basiclocation.ui.comp.PoiComponent
import com.example.basiclocation.ui.comp.PointOfInterestCardDetail
import com.example.basiclocation.viewmodels.MapViewModel
import com.example.basiclocation.viewmodels.POIViewModel
import org.osmdroid.config.Configuration

class MapActivity : ComponentActivity() {
    private val TAG = "MapActivity"

    private lateinit var locationHelper: LocationHelper
    private lateinit var googlePlayServicesHelper: GooglePlayServicesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration OSMDroid - IMPORTANT de le faire avant d'utiliser MapView
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        // Initialize helpers
        locationHelper = LocationHelper(this)
        googlePlayServicesHelper = GooglePlayServicesHelper(this)

        // Setup location permission callback
        locationHelper.setupLocationPermissionRequest(
            onPermissionGranted = {
                checkGooglePlayAndLocationSettings()
            },
            onPermissionDenied = {
                Log.d(TAG, "La localisation n'est pas encore autorisé sur cet appareil")
            }
        )

        setContent {
            val poiRepository = PoiRepository()

            // Create ViewModel
            val mapViewModel: MapViewModel = viewModel(
                factory = MapViewModel.Factory(locationHelper, poiRepository)
            )

            val poiViewModel: POIViewModel = viewModel(
                factory = POIViewModel.Factory(application, poiRepository)
            )

            var reachedPOI by remember { mutableStateOf<PointOfInterest?>(null) }
            var clickedPOI by remember { mutableStateOf<PointOfInterest?>(null) }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Map Component
                    MapComponent(
                        mapViewModel = mapViewModel,
                        poiViewModel = poiViewModel,
                        onPointOfInterestClicked = { poi ->
                            clickedPOI = poi;
                        },
                        onPointOfInterestReached = { poi ->
                            reachedPOI = poi
                        }
                    )

                    // Show POI Infos on click
                    reachedPOI?.let { poi ->
                        PoiComponent(
                            pointOfInterest = poi,
                            onDismiss = {
                                reachedPOI = null
                                mapViewModel.clearNearbyPointOfInterest()
                            }
                        )
                    }

                    // Show POI detail dialog when selected
                    clickedPOI?.let { poi ->
                        PointOfInterestCardDetail(
                            pointOfInterest = poi,
                            onDismiss = {
                                clickedPOI = null
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
                Log.d(TAG, "L'appareil est correctement configuré : Location & Google Play")
            },
            onFailure = { _ ->
                // The googlePlayServicesHelper will show the dialog to enable location
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationHelper.handleActivityResult(requestCode, resultCode)
    }

    override fun onResume() {
        super.onResume()
        // Just if configuration failed in onCreate
        if (Configuration.getInstance().userAgentValue.isNullOrEmpty()) {
            Configuration.getInstance().load(
                applicationContext,
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            )
        }
    }
}