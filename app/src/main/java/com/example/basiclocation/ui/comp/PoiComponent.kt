package com.example.basiclocation.ui.comp

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basiclocation.R
import com.example.basiclocation.model.GameType
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.ui.theme.lightSecondaryColor
import com.example.basiclocation.ui.theme.primaryColor
import com.example.basiclocation.ui.theme.thirdColor
import com.example.basiclocation.viewmodels.PuzzleState
import com.example.basiclocation.viewmodels.PuzzleViewModel
import kotlinx.coroutines.launch

@Composable
fun PoiComponent(
    pointOfInterest: PointOfInterest,
    onDismiss: () -> Unit
) {
    val viewModel: PuzzleViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current.density

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

    var contentSize by remember { mutableStateOf(Size.Zero) }
    var isPuzzleSolved by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.puzzleSolved.collect { solved ->
            isPuzzleSolved = solved
        }
    }


    // Déclenchement après la récupération de la taille de InfoTab
    LaunchedEffect(contentSize) {
        if (contentSize != Size.Zero) {
            // Convertir la taille de px en dp
            val widthDp = (contentSize.width / density).dp
            val heightDp = (contentSize.height / density).dp

            if (viewModel.puzzleState.value !is PuzzleState.Ready) {
                // Lancer la génération du puzzle
                viewModel.initPuzzle(
                    context = context,
                    drawableResId = R.drawable.vitrail,
                    availableWidth = widthDp,
                    availableHeight = heightDp,
                    itemSpacing = 2.dp,
                    baseNbCol = 4
                )
            } else {
                Log.d("ParentPoiComp", "Puzzle already initialized, skipping init.")
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = primaryColor,
                contentColor = thirdColor
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

                // TabRow pour les onglets
                val tabHeight = 36.dp
                val indicatorHeight = 3.dp
                val unselectedAlpha = 0.6f
                val roundedCorner = 8.dp
                val textColor = thirdColor

                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = lightSecondaryColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(tabHeight)
                        .clip(RoundedCornerShape(roundedCorner)),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                .padding(horizontal = 0.dp),
                            height = indicatorHeight,
                            color = thirdColor
                        )
                    }
                ) {
                    listOf("Infos", "Jeu").forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
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
                        .weight(1f)
                ) { page ->
                    when (page) {
                        0 -> InfoTab(
                            pointOfInterest = pointOfInterest,
                            onPlayClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            onContentSizeChange = { size ->
                                contentSize = size
                            }
                        )

                        1 -> when (pointOfInterest.gameType) {
                            GameType.PUZZLE -> PuzzleTab(
                                pointOfInterest.gameTitle,
                                viewModel,
                                isPuzzleSolved,
                                onClose = { onDismiss() })

                            GameType.QUIZ -> QuizzTab(
                                pointOfInterest.gameTitle,
                                onClose = { onDismiss() })
                        }
                    }
                }
            }
        }
    }
}