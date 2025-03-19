package com.example.basiclocation.ui.comp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basiclocation.model.DragItem
import com.example.basiclocation.viewmodels.GridInfo
import com.example.basiclocation.viewmodels.PuzzleState
import com.example.basiclocation.viewmodels.PuzzleViewModel

@Composable
fun PuzzleGrid(
    gridInfo: GridInfo,
) {
    val viewModel: PuzzleViewModel = viewModel()

    // Récupère l'état du puzzle
    val puzzleState = viewModel.puzzleState.collectAsState().value
    val puzzlePieces = if (puzzleState is PuzzleState.Ready && puzzleState.puzzlePieces.isNotEmpty()) {
        puzzleState.puzzlePieces
    } else {
        emptyList<DragItem>()
    }

    // Afficher la grille réorganisable
    ReorderableGrid(
        initialItems = puzzlePieces,
        cellWidth = gridInfo.cellWidth,
        cellHeight = gridInfo.cellHeight,
        nbCol = gridInfo.nbCol,
        nbRow = gridInfo.nbRow,
        itemSpacing = 2.dp,
        onItemsReordered = { newItems ->
            viewModel.updatePuzzlePositions(newItems)
        }
    )
}