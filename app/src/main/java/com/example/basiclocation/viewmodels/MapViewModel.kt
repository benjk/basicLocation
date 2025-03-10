package com.example.basiclocation.viewmodels

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.basiclocation.helpers.LocationHelper
import com.example.basiclocation.model.PointOfInterest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint

class MapViewModel(
    private val locationHelper: LocationHelper
) : ViewModel() {
    // Constants for default map position and zoom level
    object MapDefaults {
        val DEFAULT_POSITION = GeoPoint(50.73678, 2.243631) // Longuenesse Mairie
        const val DEFAULT_ZOOM = 20.0
        const val DEZOOM_MIN = 15.0
        const val ZOOM_MAX = 21.0
    }

    var initCenter = MutableLiveData(false)

    fun markCenterInitialized() {
        initCenter.value = true
    }

    private val _positionState = mutableStateOf(MapDefaults.DEFAULT_POSITION)
    val positionState: State<GeoPoint> = _positionState

    private val _zoomState = mutableStateOf(MapDefaults.DEFAULT_ZOOM)
    val zoomState: State<Double> = _zoomState

    fun updatePosition(position: GeoPoint?) {
        _positionState.value = position ?: MapDefaults.DEFAULT_POSITION
    }

    fun updateZoom(zoom: Double?) {
        Log.e("ZZZ", "nouvelle ZOOM VALUE : " + zoom)
        _zoomState.value = zoom ?: MapDefaults.DEFAULT_ZOOM
    }

    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState.asStateFlow()

    private val _pointsOfInterest = mutableStateOf<List<PointOfInterest>>(emptyList())
    val pointsOfInterest: State<List<PointOfInterest>> = _pointsOfInterest

    private val _nearbyPointOfInterest = MutableStateFlow<PointOfInterest?>(null)
    val nearbyPointOfInterest: StateFlow<PointOfInterest?> = _nearbyPointOfInterest.asStateFlow()

    init {
        // Load initial POIs (this would come from a repository in a real app)
        loadSamplePOIs()

        // Setup location updates
        locationHelper.setLocationUpdateListener { location ->
            _locationState.value = location
            checkForNearbyPOIs(location)
        }

        // Récupérer la dernière position connue au démarrage
        locationHelper.getLastLocation { location ->
            location?.let {
                _locationState.value = it
                checkForNearbyPOIs(it)
            }
        }
    }

    private fun loadSamplePOIs() {
        _pointsOfInterest.value = listOf(
            PointOfInterest(
                id = "poi_1",
                name = "Derrière le garage",
                description = "20 rue Edouard Herriot - Garage 1",
                latitude = 50.736888,
                longitude = 2.251257,
                triggerRadiusMeters = 5,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_4",
                name = "Devant le garage",
                description = "20 rue Edouard Herriot - Garage 2",
                latitude = 50.736915,
                longitude = 2.251172,
                triggerRadiusMeters = 5,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_5",
                name = "Devant la maison",
                description = "20 rue Edouard Herriot - DEVANT",
                latitude = 50.736996,
                longitude = 2.251001,
                triggerRadiusMeters = 5,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_6",
                name = "Fond du jardin",
                description = "20 rue Edouard Herriot - Jardin",
                latitude = 50.736834,
                longitude = 2.251214,
                triggerRadiusMeters = 5,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_2",
                name = "Le terrain de pétanque",
                description = "Ci-git quelques paires de boules",
                latitude = 50.744720,
                longitude = 2.248714,
                triggerRadiusMeters = 25,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_3",
                name = "Le parking des glacis",
                description = "Gare toi là et profites en pour jeter tes bouteilles vides",
                latitude = 50.744231,
                longitude = 2.247671,
                triggerRadiusMeters = 50,
                minTimeToTriggerSeconds = 10
            ),
            // Add more sample POIs as needed
        )
    }

    private fun checkForNearbyPOIs(userLocation: Location) {
        // Ne notifier que pour un nouveau POI à proximité
        if (_nearbyPointOfInterest.value == null) {
            _pointsOfInterest.value.forEach { poi ->
                if (poi.isUserNearby(userLocation)) {
                    _nearbyPointOfInterest.value = poi
                    return
                }
            }
        }
    }

    fun clearNearbyPointOfInterest() {
        _nearbyPointOfInterest.value = null
    }

    fun startLocationUpdates() {
        locationHelper.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        locationHelper.stopLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }

    class Factory(private val locationHelper: LocationHelper) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                return MapViewModel(locationHelper) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}