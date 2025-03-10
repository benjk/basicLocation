package com.example.basiclocation.ui.comp

import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.viewmodels.MapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapComponent(
    viewModel: MapViewModel,
    onPointOfInterestClicked: (PointOfInterest) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // Collect state from ViewModel
    val location by viewModel.locationState.collectAsState()
    val pointsOfInterest by viewModel.pointsOfInterest.collectAsState()
    val nearbyPOI by viewModel.nearbyPointOfInterest.collectAsState()

    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    // Gestion des POIs à proximité
    LaunchedEffect(nearbyPOI) {
        nearbyPOI?.let { onPointOfInterestClicked(it) }
    }

    // Lifecycle observer to handle location updates
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.startLocationUpdates()
                    mapViewRef.value?.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.stopLocationUpdates()
                    mapViewRef.value?.onPause()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Fonction pour mettre à jour la MapView
    fun updateMapView(
        mapView: MapView,
        location: Location?,
        pointsOfInterest: List<PointOfInterest>,
        onPointOfInterestClicked: (PointOfInterest) -> Unit,
        context: Context
    ) {
        // Mise à jour de la carte uniquement si on a une localisation
        location?.let { userLocation ->
            // Convertir la localisation en GeoPoint
            val userGeoPoint = GeoPoint(userLocation.latitude, userLocation.longitude)

            // Déplacer la caméra vers la position de l'utilisateur
            mapView.controller.animateTo(userGeoPoint)

            // Supprimer les anciens overlays
            mapView.overlays.clear()

            // Ajouter le marqueur de l'utilisateur
            val userMarker = Marker(mapView).apply {
                position = userGeoPoint
                title = "Votre position"
                icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
            }
            mapView.overlays.add(userMarker)

            // Ajouter les marqueurs des POIs
            pointsOfInterest.forEach { poi ->
                val poiMarker = Marker(mapView).apply {
                    position = GeoPoint(poi.latitude, poi.longitude)
                    title = poi.name
                    snippet = poi.description

                    // Gestion du clic
                    setOnMarkerClickListener { _, _ ->
                        onPointOfInterestClicked(poi)
                        true // Consommer l'événement
                    }
                }
                mapView.overlays.add(poiMarker)
            }

            // Forcer le rafraîchissement de la carte
            mapView.invalidate()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                // Configuration initiale de la carte
                setMultiTouchControls(true)
                setTileSource(TileSourceFactory.MAPNIK)

                // Zoom initial
                controller.setZoom(20.0)

                // Position par défaut (Paris)
                controller.setCenter(GeoPoint(48.856614, 2.3522219))

                mapViewRef.value = this
            }
        },
        update = { mapView ->
            updateMapView(mapView, location, pointsOfInterest, onPointOfInterestClicked, context)
        }
    )
}