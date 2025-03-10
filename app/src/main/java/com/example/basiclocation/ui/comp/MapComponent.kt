package com.example.basiclocation.ui.comp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.viewmodels.MapViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapComponent(
    viewModel: MapViewModel,
    onPointOfInterestClicked: (PointOfInterest) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Collect state from ViewModel
    val location by viewModel.locationState.collectAsState()
    val pointsOfInterest by viewModel.pointsOfInterest.collectAsState()
    val nearbyPOI by viewModel.nearbyPointOfInterest.collectAsState()

    // Default position (centered on default location)
    val defaultPosition = LatLng(48.856614, 2.3522219)
    val userPosition = location?.let { LatLng(it.latitude, it.longitude) }

    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userPosition ?: defaultPosition, 15f)
    }

    // Show POI detail when we detect a nearby POI
    LaunchedEffect(nearbyPOI) {
        nearbyPOI?.let { onPointOfInterestClicked(it) }
    }

    // Update camera position when user location changes
    LaunchedEffect(userPosition) {
        userPosition?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    // Lifecycle observer to handle location updates
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.startLocationUpdates()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.stopLocationUpdates()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Add markers for all POIs
            pointsOfInterest.forEach { poi ->
                Marker(
                    state = MarkerState(position = poi.getLatLng()),
                    title = poi.name,
                    snippet = poi.description,
                    onClick = {
                        onPointOfInterestClicked(poi)
                        false // Allow default behavior (showing info window)
                    }
                )
            }

            // Add marker for current user location
            userPosition?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            }
        }
    }
}