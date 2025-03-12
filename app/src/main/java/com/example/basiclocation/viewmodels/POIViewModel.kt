package com.example.basiclocation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.basiclocation.model.PointOfInterest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class POIViewModel(
    private val application: Application,
) : ViewModel() {

    private val context = application;

    // Liste des POIs à afficher
    private val _poiList = MutableLiveData<List<PointOfInterest>>()
    val poiList: LiveData<List<PointOfInterest>> = _poiList

    private val imageLoader = Coil.imageLoader(context)

    init {
        loadPOIs()
    }

    private fun loadPOIs() {
        val pois = listOf(
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
                minTimeToTriggerSeconds = 10,
                imageName = "poi_moulin"
            ),
            PointOfInterest(
                id = "poi_2",
                name = "La Cathédrale",
                description = "Notre-Dame de Saint-Omer est une église catholique datant de 1879.",
                latitude = 50.747345,
                longitude = 2.252833,
                triggerRadiusMeters = 40,
                minTimeToTriggerSeconds = 8,
                imageName = "poi_cathedrale"
            ),
            PointOfInterest(
                id = "poi_3",
                name = "Le Jardin Public",
                description = "Mêlant nature, animaux, architecture et activités diverses, ce parc est un incontournable de la ville !",
                latitude = 50.749609,
                longitude = 2.249382,
                triggerRadiusMeters = 50,
                minTimeToTriggerSeconds = 10,
                imageName = "poi_jp"
            ),
            PointOfInterest(
                id = "poi_4",
                name = "Musée Sandelin",
                description = "Le musée de l'hôtel Sandelin est un musée d'art et de l'histoire de la ville datant de 1904.",
                latitude = 50.748989,
                longitude = 2.25441,
                triggerRadiusMeters = 25,
                minTimeToTriggerSeconds = 10,
                imageName = "poi_sandelin"
            ),
            PointOfInterest(
                id = "poi_5",
                name = "La Gare",
                description = "Ce bâtiment est l'un des plus beau de la ville, et abrite aujourd'hui encore une gare en fonctionnement ainsi que la Station, un espace communautaire symbolisant la transformation du territoire, réunissant entrepreneurs, industriels, étudiants et services autour de projets innovants",
                latitude = 50.753598,
                longitude = 2.266739,
                triggerRadiusMeters = 25,
                minTimeToTriggerSeconds = 10,
                imageName = "poi_gare"
            ),
            PointOfInterest(
                id = "poi_6",
                name = "Abbaye Saint-Bertin",
                description = "Aujourd'hui en ruines, ce monument historique est une ancienne abbaye bénédictine datant de VIIème siècle.",
                latitude = 50.750633,
                longitude = 2.263839,
                triggerRadiusMeters = 45,
                minTimeToTriggerSeconds = 10,
                imageName = "poi_stbertin"
            ),
            // Add more sample POIs as needed
        )

        _poiList.value = pois

        prechargeImages(pois);
    }

    private fun prechargeImages(pois: List<PointOfInterest>) {
        viewModelScope.launch(Dispatchers.IO) {
            pois.forEach { poi ->
                try {
                    // Obtenir l'ID de ressource à partir du nom
                    val resourceId = context.resources.getIdentifier(
                        poi.imageName,
                        "drawable",
                        application.packageName
                    )

                    if (resourceId == 0) {
                        Log.e("POIViewModel", "Ressource introuvable pour ${poi.imageName}")
                        return@forEach
                    }

                    Log.d("POIViewModel", "ID de ressource obtenu pour ${poi.imageName}: $resourceId")

                    val imageRequest = ImageRequest.Builder(application)
                        .data(resourceId)
                        .listener(
                            onSuccess = { _, _ ->
                                Log.d("POIViewModel", "Image préchargée avec succès: ${poi.imageName}")
                            },
                            onError = { _, error ->
                                Log.e("POIViewModel", "Erreur de préchargement: ${poi.imageName}", error.throwable)
                            }
                        )
                        .build()

                    imageLoader.enqueue(imageRequest)
                } catch (e: Exception) {
                    Log.e("POIViewModel", "Exception lors du préchargement de ${poi.imageName}", e)
                }
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(POIViewModel::class.java)) {
                return POIViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
