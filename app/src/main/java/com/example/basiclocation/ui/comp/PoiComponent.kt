package com.example.basiclocation.ui.comp

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.ui.theme.lightSecondaryColor
import com.example.basiclocation.ui.theme.primaryColor
import com.example.basiclocation.ui.theme.thirdColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PoiComponent(
    pointOfInterest: PointOfInterest,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // État pour gérer les onglets
    var selectedTab by remember { mutableStateOf(0) }

    // Conversion du nom en ID de ressource
    val resourceId = remember(pointOfInterest.imageName) {
        pointOfInterest.imageName?.let {
            context.resources.getIdentifier(
                it,
                "drawable",
                context.packageName
            )
        } ?: 0
    }

    // Configuration du pager
    val pagerState = rememberPagerState { 2 }

    // Pour synchroniser l'onglet sélectionné avec le pager
    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
        Log.d("PARDI", "PLZ")
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
        Log.d("PARDI", "STOP")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.9f)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()  // Prend 80% de la hauteur de l'écran
                .padding(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = thirdColor,
                contentColor = primaryColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image et titre
                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Image de ${pointOfInterest.name}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    text = pointOfInterest.name,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // TabRow pour les onglets
                val tabHeight = 36.dp
                val indicatorHeight = 6.dp
                val unselectedAlpha = 0.6f
                val roundedCorner = 8.dp
                val textColor = thirdColor

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = lightSecondaryColor,
                    contentColor = primaryColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(tabHeight)
                        .clip(RoundedCornerShape(roundedCorner)),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]).border(2.dp, lightSecondaryColor).padding(horizontal = 0.dp),
                            height = indicatorHeight,
                            color = thirdColor
                        )
                    }
                ) {
                    listOf("Infos", "Jeu").forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.height(tabHeight),
                            text = {
                                Text(
                                    title.uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            },
                            selectedContentColor = textColor,
                            unselectedContentColor = textColor.copy(alpha = unselectedAlpha)
                        )
                    }
                }

                // Contenu des onglets avec HorizontalPager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    when (page) {
                        0 -> InfoTab(pointOfInterest) { selectedTab = 1 }
                        1 -> GameTab(pointOfInterest.id)
                    }
                }
            }
        }
    }
}