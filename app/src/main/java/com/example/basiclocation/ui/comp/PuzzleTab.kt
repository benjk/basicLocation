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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.basiclocation.viewmodels.PuzzleState
import com.example.basiclocation.viewmodels.PuzzleViewModel

@Composable
fun PuzzleTab(gameTitle : String, viewModel: PuzzleViewModel, isPuzzleSolved: Boolean) {
    val puzzleState by viewModel.puzzleState.collectAsState()

    TabComponent(
        title = gameTitle,
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
