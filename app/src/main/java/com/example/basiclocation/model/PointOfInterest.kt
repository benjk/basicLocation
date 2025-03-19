package com.example.basiclocation.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class PointOfInterest(
    val id: String,
    val name: String,
    val description: String,
    val texts: List<String> = ArrayList(),
    val titles: List<String> = ArrayList(),
    val gameTitle: String = "",
    val latitude: Double,
    val longitude: Double,
    val triggerRadiusMeters: Int = 50,
    val minTimeToTriggerSeconds: Int = 5,
    val imageName: String? = null,
    var reached: Boolean = false
) {
    fun getLatLng(): LatLng = LatLng(latitude, longitude)

    fun isUserNearby(userLocation: Location): Boolean {
        val poiLocation = Location("POI").apply {
            latitude = this@PointOfInterest.latitude
            longitude = this@PointOfInterest.longitude
        }
        return poiLocation.distanceTo(userLocation) <= triggerRadiusMeters
    }
}