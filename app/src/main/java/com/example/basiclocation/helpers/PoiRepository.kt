package com.example.basiclocation.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.basiclocation.model.PointOfInterest

class PoiRepository {
    private val _pois = MutableLiveData<List<PointOfInterest>>(emptyList())
    val pois: LiveData<List<PointOfInterest>> = _pois

    fun setPois(newPois: List<PointOfInterest>) {
        _pois.value = newPois
    }
}
