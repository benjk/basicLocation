package com.example.basiclocation.ui.comp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.basiclocation.ui.theme.Typography
import com.example.basiclocation.ui.theme.lightSecondaryColor
import com.example.basiclocation.ui.theme.thirdColor

@Composable
fun TabComponent(
    title: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    buttonEnabled: Boolean = true,
    onContentSizeChange: (Size) -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Titre en haut
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = Typography.headlineSmall,
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onContentSizeChange(coordinates.size.toSize())
                }
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bouton en bas
        Button(
            onClick = onButtonClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = lightSecondaryColor,
                contentColor = thirdColor
            ),
            enabled = buttonEnabled
        ) {
            Text(buttonText, style = MaterialTheme.typography.labelLarge)
        }
    }
}