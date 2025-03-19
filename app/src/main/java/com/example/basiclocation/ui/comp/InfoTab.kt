package com.example.basiclocation.ui.comp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.ui.theme.Typography
import com.example.basiclocation.ui.theme.primaryColor
import com.example.basiclocation.ui.theme.thirdColor

@Composable
fun InfoTab(
    pointOfInterest: PointOfInterest,
    onPlayClick: () -> Unit,
    onContentSizeChange: (Size) -> Unit
) {
    val scrollState = rememberScrollState()

    TabComponent(
        title = pointOfInterest.name,
        buttonText = "JOUER !",
        onButtonClick = onPlayClick,
        onContentSizeChange = onContentSizeChange
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                pointOfInterest.texts.getOrNull(0)?.let { introText ->
                    Text(
                        text = introText,
                        style = Typography.bodySmall,
                        textAlign = TextAlign.Justify
                    )
                }

                pointOfInterest.texts.drop(1).forEachIndexed { index, text ->
                    pointOfInterest.titles.getOrNull(index)?.let { title ->
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = title,
                            style = Typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Affichage du texte
                    Text(
                        text = text,
                        style = Typography.bodySmall,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            AnimatedVisibility(
                visible = scrollState.canScrollForward,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Dégradé subtil sur le contenu (plus léger et élégant)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        thirdColor.copy(alpha = 0.5f),
                                        thirdColor.copy(alpha = 0.9f),
                                    ),
                                    startY = 0f,
                                    endY = 40f
                                )
                            )
                    )

                    // Ligne indicatrice en dessous du dégradé
                    Box(
                        modifier = Modifier
                            .padding(top = 36.dp)
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(2.dp)
                            .padding(horizontal = 32.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        primaryColor.copy(alpha = 0.3f),
                                        primaryColor.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}
