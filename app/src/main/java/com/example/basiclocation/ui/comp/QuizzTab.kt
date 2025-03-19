package com.example.basiclocation.ui.comp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun QuizzTab(gameTitle : String) {
    TabComponent(
        title = gameTitle,
        buttonText = "CONTINUER",
        onButtonClick = {},
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("ICI ON AURA UN BO QUIZ")
        }
    }

}