package com.example.basiclocation.ui.comp

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.basiclocation.R
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.viewmodels.MapViewModel
import com.example.basiclocation.viewmodels.POIViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@Composable
fun MapComponent(
    mapViewModel: MapViewModel,
    poiViewModel: POIViewModel,
    onPointOfInterestClicked: (PointOfInterest) -> Unit,
    onPointOfInterestReached: (PointOfInterest) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // Collect state from ViewModel
    val location by mapViewModel.locationState.collectAsState()
    val nearbyPOI by mapViewModel.nearbyPointOfInterest.collectAsState()
    val pointsOfInterest = poiViewModel.poiList.value

    // Pour ne centrer la carte que la première fois (au démarrage)
    val initCenter = mapViewModel.initCenter.observeAsState(false)

    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    // Gestion des POIs à proximité
    LaunchedEffect(nearbyPOI) {
        nearbyPOI?.let { poi ->
            if (!poi.reached) {
                // Centre la carte sur le POI trouvé
                mapViewRef.value?.controller?.animateTo(GeoPoint(poi.latitude, poi.longitude))

                poi.reached = true
                updatePoi(context, mapViewRef.value, poi)

                onPointOfInterestReached(poi)
            }
        }
    }

    // Lifecycle observer to handle location updates
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapViewModel.startLocationUpdates()
                    mapViewRef.value?.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    mapViewModel.stopLocationUpdates()
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
        context: Context
    ) {
        // Mise à jour de la carte uniquement si on a une localisation
        location?.let { userLocation ->
            // Convertir la localisation en GeoPoint
            val userGeoPoint = GeoPoint(userLocation.latitude, userLocation.longitude)

            // Déplacer la caméra vers la position de l'utilisateur **uniquement au démarrage**
            if (!initCenter.value) {
                mapView.controller.animateTo(userGeoPoint)
                mapViewModel.markCenterInitialized()
            }

            // Supprimer uniquement les overlays qui ne sont pas des marqueurs de POI
            mapView.overlays.removeAll { overlay ->
                overlay !is Marker || overlay.id == null // Garder les marqueurs de POI (ceux avec un ID)
            }

            // Ajouter le marqueur de l'utilisateur en premier pour qu'il soit derrière
            val userMarker = Marker(mapView).apply {
                position = userGeoPoint
                title = "Votre position"
                icon = ContextCompat.getDrawable(context, R.drawable.user_icon)
            }
            mapView.overlays.add(0, userMarker)

            // Ajouter les marqueurs des POI
            pointsOfInterest?.forEach { poi ->
                val existingMarker = mapView.overlays.find { it is Marker && it.id == poi.id } as? Marker
                if (existingMarker == null) {
                    val iconId = if (poi.reached) R.drawable.location_pin_reached else R.drawable.location_pin
                    val iconRes = ContextCompat.getDrawable(context, iconId)
                    iconRes?.alpha = (0.7 * 255).toInt()
                    val poiMarker = Marker(mapView).apply {
                        position = GeoPoint(poi.latitude, poi.longitude)
                        title = poi.name
                        id = poi.id
                        icon = iconRes
                        setOnMarkerClickListener { _, _ ->
                            CoroutineScope(Dispatchers.Main).launch {
                                val startTime = System.currentTimeMillis()

                                delay(100) // Laissez un peu de temps à OSMDroid
                                if (poi.reached) {
                                    onPointOfInterestReached(poi)
                                } else {
                                    onPointOfInterestClicked(poi)
                                }

                                val endTime = System.currentTimeMillis()
                                Log.d("PERF", "Temps de traitement du clic : ${endTime - startTime} ms")
                            }
                            true
                        }
                    }
                    mapView.overlays.add(poiMarker)
                }
            }

            // Forcer le rafraîchissement de la carte
            mapView.invalidate()
        }
    }

//    fun updateMarkerSizes(zoomLevel: Double) {
////        if (zoomLevel == mapViewModel.zoomState.value) { return }
//        mapViewModel.updateZoom(zoomLevel)
//        val mapView = mapViewRef.value ?: return
//
//        val baseSize = 100
//        val minSize = 20
//        val size = (baseSize * (zoomLevel / 20)).toInt().coerceAtLeast(minSize)
//
//        mapView.overlays.forEach { overlay ->
//
//            if (overlay is Marker) {
//                val icon = overlay.icon
//
//                val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(bitmap)
//                icon.setBounds(0, 0, canvas.width, canvas.height)
//                icon.draw(canvas)
//                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false)
//                val resizedIcon = BitmapDrawable(context.resources, resizedBitmap)
//                Log.d("kk", "kkkkkkkkkk : " + icon)
//
//                overlay.icon = resizedIcon
//            }
//        }
//
//        mapView.invalidate()
//    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                // Configuration initiale de la carte
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                setTileSource(TileSourceFactory.MAPNIK)

                controller.setZoom(mapViewModel.zoomState.value)
                controller.setCenter(mapViewModel.positionState.value)

                minZoomLevel = MapViewModel.MapDefaults.DEZOOM_MIN
                maxZoomLevel = MapViewModel.MapDefaults.ZOOM_MAX

                this.addMapListener(object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean {
                        val currentPosition = this@apply.mapCenter
                        if (currentPosition is GeoPoint) {
                            mapViewModel.updatePosition(currentPosition)
                        } else {
                            Log.e("Map", "La position n'est pas un GeoPoint")
                        }
                        return false
                    }

                    override fun onZoom(event: ZoomEvent?): Boolean {
                        val zoom = event?.zoomLevel
                        if (zoom != null && (zoom < maxZoomLevel || zoom > minZoomLevel)) {
                            mapViewModel.updateZoom(zoom)
                        }
                        return false
                    }
                })

                mapViewRef.value = this
            }
        },
        update = { mapView ->
            updateMapView(mapView, location, context)
        }
    )
}

fun updatePoi(context: Context, mapView: MapView?, poi: PointOfInterest) {
    if (mapView != null) {
        val existingMarker = mapView.overlays.find { it is Marker && it.id == poi.id } as? Marker

        if (existingMarker != null) {
            // Mettre à jour l'icône du marqueur si nécessaire
            val iconId = if (poi.reached) R.drawable.location_pin_reached else R.drawable.location_pin
            if (existingMarker.icon != ContextCompat.getDrawable(context, iconId)) {
                existingMarker.icon = ContextCompat.getDrawable(context, iconId)
            }

            // Rafraîchir la carte pour afficher les changements
            mapView.invalidate()
        }
    }
}