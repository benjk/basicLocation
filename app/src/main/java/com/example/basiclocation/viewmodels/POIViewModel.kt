package com.example.basiclocation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.request.ImageRequest
import com.example.basiclocation.helpers.PoiRepository
import com.example.basiclocation.model.GameType
import com.example.basiclocation.model.PointOfInterest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class POIViewModel(
    private val application: Application,
    private val poiRepository: PoiRepository
) : ViewModel() {

    private val context = application

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
                name = "Ma Maison",
                description = "Notre-Dame de Saint-Omer est une église catholique datant du XIᵉ siècle, qui a traversé les siècles et continue de témoigner de l’histoire de la ville. Ce monument exceptionnel allie richesse architecturale et patrimoine culturel, offrant un intérieur où l’art et l’histoire se rencontrent à chaque coin.",
                texts = listOf(
                    "La cathédrale Notre-Dame de Saint-Omer est une église catholique datant du XVIᵉ siècle. Elle est toujours en activité et elle est indéniablement l'un des plus beaux monuments historiques de notre ville.",
                    "La construction de la cathédrale a débuté au XIᵉ siècle et durera près de 5 siècles. À l'origine une chapelle, puis une église, c'est après un incendie en 1033 qu'on décide de la reconstruire en pierre, dans un style roman. C'est à cette époque qu'ont été posées les premières pierres que l'on retrouve encore aujourd'hui. Avec le temps, d'autres types d'architectures ont été utilisés et c'est devenu un mélange de roman, de gothique primitif et de gothique rayonnant.",
                    "La cathédrale de Saint-Omer regorge de richesses artistiques et historiques. Parmi ses trésors, l'horloge astrolabe du XVIᵉ siècle et le magnifique buffet d'orgue du XVIIIᵉ siècle en font un lieu incontournable pour les amateurs d'art et d'architecture. On y trouve également des œuvres remarquables comme le tableau de Rubens représentant la Descente de la Croix, ainsi que des sculptures fascinantes, telles que la Vierge au Chat et la scène biblique de Shadrach, Méshach et Abed-Nego dans la fournaise. Le triptyque du Grand Dieu de Thérouanne, les dalles du XIIIᵉ siècle et un dallage représentant un pèlerin à quatre chaussures complètent ce patrimoine exceptionnel. Chaque élément de la cathédrale raconte une part de l’histoire et de la culture de Saint-Omer."
                ),
                titles = listOf(
                    "La construction",
                    "Les trésors de la cathédrale"
                ),

                gameTitle = "Réponds aux questions !",
                latitude = 50.736942,
                longitude = 2.251044,
                triggerRadiusMeters = 15,
                minTimeToTriggerSeconds = 6,
                imageName = "poi_cathedrale",
                gameType = GameType.QUIZ
            ),
            PointOfInterest(
                id = "poi_1",
                name = "La Grand-Place",
                description = "Le centre névralgique de la cité audomaroise, où l'on retrouve le fameux Moulin à café, ancien Hôtel de ville devenu aujourd'hui un théâtre.",
                texts = listOf(
                    "La Grand-Place de Saint-Omer est un lieu où l’histoire et la modernité se rencontrent harmonieusement. Cœur battant de la ville, cette place est un véritable symbole du dynamisme de Saint-Omer, mêlant patrimoine et vie contemporaine.",
                    "Le bâtiment emblématique de la place, surnommé le \"Moulin à café\", est un vestige industriel qui a marqué l’histoire de la ville. Construit au XIXe siècle, il représente l’architecture typique de l’époque, avec sa silhouette distinctive rappelant un moulin à vent. Ce bâtiment a joué un rôle majeur dans l’activité commerciale et industrielle locale, et aujourd’hui, il reste un témoin du passé de Saint-Omer.",
                    "Aujourd’hui, la Grand-Place est un espace animé et vivant, avec des bars, des terrasses et des restaurants qui attirent les habitants et les visiteurs. Elle accueille le marché de la ville tous les samedis matin. L'un des points phares de cette place est le théâtre \"La Barcarolle\", un lieu culturel incontournable de l'audomarois."
                ),
                titles = listOf(
                    "Un peu d'histoire",
                    "Aujourd'hui"
                ),
                gameTitle = "Réponds aux questions !",
                latitude = 50.750067,
                longitude = 2.251813,
                triggerRadiusMeters = 25,
                minTimeToTriggerSeconds = 10,
                imageName = "poi_moulin",
                gameType = GameType.QUIZ
            ),
            PointOfInterest(
                id = "poi_2",
                name = "La Cathédrale",
                description = "Notre-Dame de Saint-Omer est une église catholique datant du XIᵉ siècle, qui a traversé les siècles et continue de témoigner de l’histoire de la ville. Ce monument exceptionnel allie richesse architecturale et patrimoine culturel, offrant un intérieur où l’art et l’histoire se rencontrent à chaque coin.",
                texts = listOf(
                    "La cathédrale Notre-Dame de Saint-Omer est une église catholique datant du XVIᵉ siècle. Elle est toujours en activité et elle est indéniablement l'un des plus beaux monuments historiques de notre ville.",
                    "La construction de la cathédrale a débuté au XIᵉ siècle et durera près de 5 siècles. À l'origine une chapelle, puis une église, c'est après un incendie en 1033 qu'on décide de la reconstruire en pierre, dans un style roman. C'est à cette époque qu'ont été posées les premières pierres que l'on retrouve encore aujourd'hui. Avec le temps, d'autres types d'architectures ont été utilisés et c'est devenu un mélange de roman, de gothique primitif et de gothique rayonnant.",
                    "La cathédrale de Saint-Omer regorge de richesses artistiques et historiques. Parmi ses trésors, l'horloge astrolabe du XVIᵉ siècle et le magnifique buffet d'orgue du XVIIIᵉ siècle en font un lieu incontournable pour les amateurs d'art et d'architecture. On y trouve également des œuvres remarquables comme le tableau de Rubens représentant la Descente de la Croix, ainsi que des sculptures fascinantes, telles que la Vierge au Chat et la scène biblique de Shadrach, Méshach et Abed-Nego dans la fournaise. Le triptyque du Grand Dieu de Thérouanne, les dalles du XIIIᵉ siècle et un dallage représentant un pèlerin à quatre chaussures complètent ce patrimoine exceptionnel. Chaque élément de la cathédrale raconte une part de l’histoire et de la culture de Saint-Omer."
                ),
                titles = listOf(
                    "La construction",
                    "Les trésors de la cathédrale"
                ),

                gameTitle = "Remets les pièces dans l'ordre !",
                latitude = 50.747345,
                longitude = 2.252833,
                triggerRadiusMeters = 70,
                minTimeToTriggerSeconds = 8,
                imageName = "poi_cathedrale",
                gameType = GameType.PUZZLE
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
        poiRepository.setPois(pois)

        prechargeImages(pois)
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

                    Log.d(
                        "POIViewModel",
                        "ID de ressource obtenu pour ${poi.imageName}: $resourceId"
                    )

                    val imageRequest = ImageRequest.Builder(application)
                        .data(resourceId)
                        .listener(
                            onSuccess = { _, _ ->
                                Log.d(
                                    "POIViewModel",
                                    "Image préchargée avec succès: ${poi.imageName}"
                                )
                            },
                            onError = { _, error ->
                                Log.e(
                                    "POIViewModel",
                                    "Erreur de préchargement: ${poi.imageName}",
                                    error.throwable
                                )
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


    class Factory(private val application: Application, private val poiRepository: PoiRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(POIViewModel::class.java)) {
                return POIViewModel(application, poiRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
