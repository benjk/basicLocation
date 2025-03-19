package com.example.basiclocation.ui.comp

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.basiclocation.R
import com.example.basiclocation.model.DragItem
import com.example.basiclocation.ui.theme.secondaryColor

@Composable
fun GameTab(context: Context) {
    var items by remember {
        mutableStateOf(
            listOf(
                DragItem("Item 1", "Content 1"),
                DragItem("Item 2", "Content 2"),
                DragItem("Item 3", "Content 2"),
                DragItem("Item 4", "Content 2"),
                DragItem("Item 5", "Content 2"),
                DragItem("Item 6", "Content 2"),
                DragItem("Item 7", "Content 2"),
                DragItem("Item 8", "Content 2"),
                DragItem("Item 9", "Content 2"),
                DragItem("Item 10", "Content 2"),
                DragItem("Item 11", "Content 3"),
                DragItem("Item 12", "Content 12")
            )
        )
    }

    val density = LocalDensity.current
    var availableWidth by remember { mutableStateOf(0.dp) }
    var availableHeight by remember { mutableStateOf(0.dp) }
    var isMeasured by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                availableWidth = with(density) { size.width.toDp() }
                availableHeight = with(density) { size.height.toDp() }
                isMeasured = true
                Log.d("zzz", "availableWidth: $availableWidth, availableHeight: $availableHeight")
            },
        contentAlignment = Alignment.Center
    ) {
        Spacer(modifier = Modifier.height(22.dp))

        // On affiche PuzzleGrid seulement si la taille a été mesurée
        if (isMeasured) {
            PuzzleGrid(
                drawableResId = R.drawable.vitrail,
                availableWidth = availableWidth,
                availableHeight = availableHeight,
                itemSpacing = 4.dp
            )
        }
    }
}
