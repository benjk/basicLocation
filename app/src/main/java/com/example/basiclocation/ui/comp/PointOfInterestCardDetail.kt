package com.example.basiclocation.ui.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.basiclocation.model.PointOfInterest
import com.example.basiclocation.ui.theme.Typography
import com.example.basiclocation.ui.theme.lightSecondaryColor
import com.example.basiclocation.ui.theme.primaryColor
import com.example.basiclocation.ui.theme.secondaryColor
import com.example.basiclocation.ui.theme.thirdColor

@Composable
fun PointOfInterestCardDetail(
    pointOfInterest: PointOfInterest,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

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

    val moreInfosText = "Rendez-vous sur place pour en savoir plus !"
    val btnText = "OK"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)  // Prend 95% de la largeur de l'écran
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Image de ${pointOfInterest.name}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = pointOfInterest.name,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold

                )

                Spacer(modifier = Modifier.height(8.dp))

                Divider(
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                        .fillMaxWidth(0.7f),
                    thickness = 1.dp,
                    color = secondaryColor.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pointOfInterest.description,
                    style = Typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = moreInfosText,
                    style = Typography.bodySmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightSecondaryColor,
                        contentColor = thirdColor
                    )
                ) {
                    Text(btnText, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}