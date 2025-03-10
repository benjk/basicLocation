package com.example.basiclocation.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.basiclocation.helpers.LocationHelper
import com.example.basiclocation.model.PointOfInterest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val locationHelper: LocationHelper
) : ViewModel() {
    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState.asStateFlow()

    private val _pointsOfInterest = MutableStateFlow<List<PointOfInterest>>(emptyList())
    val pointsOfInterest: StateFlow<List<PointOfInterest>> = _pointsOfInterest.asStateFlow()

    private val _nearbyPointOfInterest = MutableStateFlow<PointOfInterest?>(null)
    val nearbyPointOfInterest: StateFlow<PointOfInterest?> = _nearbyPointOfInterest.asStateFlow()

    private var poiProximityTimeMap = mutableMapOf<String, Long>()

    init {
        // Setup location updates
        locationHelper.setLocationUpdateListener { location ->
            _locationState.value = location
            checkForNearbyPOIs(location)
        }

        // Load initial POIs (this would come from a repository in a real app)
        loadSamplePOIs()
    }

    private fun loadSamplePOIs() {
        _pointsOfInterest.value = listOf(
            PointOfInterest(
                id = "poi_1",
                name = "La Maison",
                description = "20 rue Edouard Herriot",
                latitude = 50.7367584,
                longitude = 2.251196,
                triggerRadiusMeters = 50,
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
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()

            for (poi in _pointsOfInterest.value) {
                if (poi.isUserNearby(userLocation)) {
                    // User is near this POI
                    if (!poiProximityTimeMap.containsKey(poi.id)) {
                        // First time near this POI - record time
                        poiProximityTimeMap[poi.id] = currentTime
                    } else {
                        // Was already near - check duration
                        val firstSeenTime = poiProximityTimeMap[poi.id] ?: currentTime
                        val durationSeconds = (currentTime - firstSeenTime) / 1000

                        // If user has been near POI for minimum required time
                        if (durationSeconds >= poi.minTimeToTriggerSeconds) {
                            _nearbyPointOfInterest.value = poi
                        }
                    }
                } else {
                    // User is not near this POI anymore
                    poiProximityTimeMap.remove(poi.id)
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