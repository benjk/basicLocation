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
                id = "poi_0",
                name = "Ma maison",
                description = "Un haut lieu de la tech du secteur audomarois",
                latitude = 50.736942,
                longitude = 2.251044,
                triggerRadiusMeters = 5,
                minTimeToTriggerSeconds = 6
            ),
            PointOfInterest(
                id = "poi_1",
                name = "La Grand-Place",
                description = "Le centre de névralgique de la cité audomaroise, on y retrouve le fameux Moulin à café, ancien hHôtel de ville aujourd'hui devenu un théâtre.",
                latitude = 50.750067,
                longitude = 2.251813,
                triggerRadiusMeters = 20,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_2",
                name = "La Cathédrale",
                description = "Notre-Dame de Saint-Omer est une église catholique datant de 1879.",
                latitude = 50.747345,
                longitude = 2.252833,
                triggerRadiusMeters = 40,
                minTimeToTriggerSeconds = 8
            ),
            PointOfInterest(
                id = "poi_3",
                name = "Le Jardin Public",
                description = "Mêlant nature, animaux, architecture et activités diverses, ce parc est un incontournable de la ville !",
                latitude = 50.749609,
                longitude = 2.249382,
                triggerRadiusMeters = 50,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_4",
                name = "Musée Sandelin",
                description = "Le musée de l'hôtel Sandelin est un musée d'art et de l'histoire de la ville datant de 1904.",
                latitude = 50.748989,
                longitude = 2.25441,
                triggerRadiusMeters = 25,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_5",
                name = "La Gare",
                description = "Ce bâtiment est l'un des plus beau de la ville, et abrite aujourd'hui encore une gare en fonctionnement ainsi que la Station, un espace communautaire symbolisant la transformation du territoire, réunissant entrepreneurs, industriels, étudiants et services autour de projets innovants",
                latitude = 50.753598,
                longitude = 2.266739,
                triggerRadiusMeters = 25,
                minTimeToTriggerSeconds = 10
            ),
            PointOfInterest(
                id = "poi_6",
                name = "Abbaye Saint-Bertin",
                description = "Aujourd'hui en ruines, ce monument historique est une ancienne abbaye bénédictine datant de VIIème siècle.",
                latitude = 50.750633,
                longitude = 2.263839,
                triggerRadiusMeters = 45,
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