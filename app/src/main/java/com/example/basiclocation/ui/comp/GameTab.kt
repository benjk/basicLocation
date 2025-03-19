package com.example.basiclocation.ui.comp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.basiclocation.model.DragItem
import com.example.basiclocation.ui.theme.primaryColor
import com.example.basiclocation.ui.theme.secondaryColor
import com.example.basiclocation.viewmodels.PuzzleState
import com.example.basiclocation.viewmodels.PuzzleViewModel

@Composable
fun GameTab(viewModel: PuzzleViewModel, isPuzzleSolved: Boolean) {
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
    val puzzleState by viewModel.puzzleState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                val availableWidth = with(density) { size.width.toDp() }
                val availableHeight = with(density) { size.height.toDp() }
                Log.d("ZZZ", "width : " + availableWidth)
                Log.d("ZZZ", "width : " + availableHeight)
            },
        contentAlignment = Alignment.Center
    ) {
        Spacer(modifier = Modifier.height(22.dp))

        when (val state = puzzleState) {
            is PuzzleState.Idle -> {
                // État initial, ne rien afficher
            }
            is PuzzleState.Loading -> {
                CircularProgressIndicator()
            }
            is PuzzleState.Ready -> {
                PuzzleGrid(
                    gridInfo = state.gridInfo,
                )
            }
            is PuzzleState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (isPuzzleSolved) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(secondaryColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Félicitations !",
                    color= primaryColor
                )
            }
        }

//        ReorderableGrid(
//            initialItems = items,
//            cellWidth = 80.dp,
//            cellHeight = 80.dp,
//            nbCol = 3,
//            nbRow = 4,
//            itemSpacing = 4.dp
//        )
    }
}
