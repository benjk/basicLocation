package com.example.basiclocation.ui.comp

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.basiclocation.model.DragItem
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

    val puzzleState by viewModel.puzzleState.collectAsState()

    TabComponent(
        title = "Remet les pièces dans l'ordre !",
        buttonText = "CONTINUER",
        onButtonClick = {},
        buttonEnabled = isPuzzleSolved
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
                Log.d("ZZZ", "state ! " + puzzleState.toString())
            }
        }
    }

}
