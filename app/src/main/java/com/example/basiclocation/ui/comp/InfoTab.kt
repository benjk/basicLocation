package com.example.basiclocation.ui.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.ui.theme.Typography
import com.example.basiclocation.ui.theme.lightSecondaryColor
import com.example.basiclocation.ui.theme.thirdColor

@Composable
fun InfoTab(
    pointOfInterest: PointOfInterest,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Section Histoire
        Text(
            text = "Histoire",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = pointOfInterest.description,
            style = Typography.bodyLarge,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section Informations pratiques
        Text(
            text = "Informations pratiques",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Rendez-vous sur place pour en savoir plus !Rendez-vous sur place pour en savoir plus !",
            style = Typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section À découvrir
        Text(
            text = "À découvrir",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Découvrez ce lieu emblématique à travers un jeu interactif !",
            style = Typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton pour passer à l'onglet jeu
        Button(
            onClick = onPlayClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = lightSecondaryColor,
                contentColor = thirdColor
            )
        ) {
            Text("JOUER !", style = MaterialTheme.typography.labelLarge)
        }
    }
}