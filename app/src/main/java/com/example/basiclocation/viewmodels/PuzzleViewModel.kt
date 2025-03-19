package com.example.basiclocation.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.basiclocation.model.DragItem
import com.example.basiclocation.model.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PuzzleViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext

    // État du puzzle
    private val _puzzleState = MutableStateFlow<PuzzleState>(PuzzleState.Idle)
    val puzzleState: StateFlow<PuzzleState> = _puzzleState.asStateFlow()

    private val _puzzleSolved = MutableStateFlow(false) // Valeur initiale false
    val puzzleSolved: StateFlow<Boolean> = _puzzleSolved.asStateFlow()

    // Cache pour stocker les dimensions de la dernière initialisation
    private var lastInitParams: InitParams? = null

    init {
        viewModelScope.launch {
            _puzzleSolved.emit(false)
        }
    }

    // Initialiser le puzzle
    fun initPuzzle(
        context: Context,
        drawableResId: Int,
        availableWidth: Dp,
        availableHeight: Dp,
        itemSpacing: Dp = 4.dp,
        baseNbCol: Int = 4
    ) {
        // Éviter de recalculer si les paramètres sont les mêmes
        val newParams =
            InitParams(drawableResId, availableWidth, availableHeight, itemSpacing, baseNbCol)
        if (lastInitParams == newParams && puzzleState.value is PuzzleState.Ready) {
            return
        }

        lastInitParams = newParams
        _puzzleState.value = PuzzleState.Loading

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val bitmap = loadBitmap(context, drawableResId)
                bitmap?.let {
                    processPuzzle(
                        context,
                        it,
                        availableWidth,
                        availableHeight,
                        itemSpacing,
                        baseNbCol
                    )
                } ?: run {
                    _puzzleState.value = PuzzleState.Error("Failed to load image")
                }
            }
        }
    }

    // Vérifie si le puzzle est résolu
    fun checkPuzzleSolved(): Boolean {
        // Vérifie que l'état est prêt et que gridInfo est disponible
        val readyState = _puzzleState.value as? PuzzleState.Ready ?: return false
        val gridInfo = readyState.gridInfo
        val positions = readyState.puzzlePieces

        // Vérifie si chaque pièce est à sa position correcte
        return positions.withIndex().all { (index, item) ->
            val row = index / gridInfo.nbCol
            val col = index % gridInfo.nbCol
            item.correctPosition?.row == row && item.correctPosition.col == col
        }
    }

    fun updatePuzzlePositions(newPositions: List<DragItem>) {
        val currentState = _puzzleState.value

        if (currentState is PuzzleState.Ready) {
            val updatedState = currentState.copy(
                puzzlePieces = newPositions
            )

            _puzzleState.value = updatedState

            if (checkPuzzleSolved()) {
                _puzzleSolved.value = true
            } else {
                _puzzleSolved.value = false
            }
        }
    }

    // Traiter l'image et créer le puzzle
    private fun processPuzzle(
        context: Context,
        bitmap: Bitmap,
        availableWidth: Dp,
        availableHeight: Dp,
        itemSpacing: Dp,
        baseNbCol: Int
    ) {
        val density = context.resources.displayMetrics.density

        // Convertir les Dp en pixels
        val availableWidthPx = availableWidth.value * density
        val availableHeightPx = availableHeight.value * density
        val spacingPx = itemSpacing.value * density

        // Calculer les dimensions en fonction du ratio de l'image
        val imageRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val nbCol = when {
            imageRatio > 1 -> baseNbCol + 1  // Paysage
            else -> baseNbCol - 1  // Portrait
        }

        val widthRatio = availableWidthPx / bitmap.width.toFloat()
        val heightRatio = availableHeightPx / bitmap.height.toFloat()

        // Détermine quelle dimension est limitante
        val (gridWidthPx, gridHeightPx) = if (widthRatio < heightRatio) {
            // La largeur est limitante, on prend toute la largeur disponible
            val gridWidth = availableWidthPx
            val gridHeight = gridWidth / imageRatio
            gridWidth to gridHeight
        } else {
            // La hauteur est limitante, on prend toute la hauteur disponible
            val gridHeight = availableHeightPx
            val gridWidth = gridHeight * imageRatio
            gridWidth to gridHeight
        }

        // Calculer la taille des cellules
        val cellWidthPx = (gridWidthPx - (nbCol + 1) * spacingPx) / nbCol
        var cellHeightPx = cellWidthPx

        val nbRow = ((gridHeightPx) / (cellHeightPx)).toInt()
        cellHeightPx = (gridHeightPx - ((nbRow + 1) * spacingPx)) / nbRow

        if (cellHeightPx > 0 && cellWidthPx > 0) {
            // Créer les pièces du puzzle
            val puzzlePieces = splitImageIntoPuzzlePieces(
                context,
                bitmap,
                nbCol,
                nbRow,
                cellWidthPx,
                cellHeightPx
            )

            // Convertir les px en dp pour les composants Compose
            val cellWidthDp = cellWidthPx / density
            val cellHeightDp = cellHeightPx / density

            // Mettre à jour l'état
            _puzzleState.value = PuzzleState.Ready(
                puzzlePieces = puzzlePieces,
                gridInfo = GridInfo(
                    nbCol = nbCol,
                    nbRow = nbRow,
                    cellWidth = cellWidthDp.dp,
                    cellHeight = cellHeightDp.dp
                )
            )
        }
    }

    // Méthodes auxiliaires pour le traitement de l'image
    private fun loadBitmap(context: Context, drawableResId: Int): Bitmap? {
        return try {
            BitmapFactory.decodeResource(context.resources, drawableResId)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun splitImageIntoPuzzlePieces(
        context: Context,
        bitmap: Bitmap,
        nbCol: Int,
        nbRow: Int,
        cellWidthPx: Float,
        cellHeightPx: Float,
    ): List<DragItem> {
        val pieces = mutableListOf<DragItem>()

        // Redimensionner le bitmap
        val bitmapHeight = cellHeightPx * nbRow
        val bitmapWidth = cellWidthPx * nbCol

        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            bitmapWidth.toInt(),
            bitmapHeight.toInt(),
            true
        )

        // Découper l'image
        for (row in 0 until nbRow) {
            for (col in 0 until nbCol) {
                val startX = (col * cellWidthPx).toInt()
                val startY = (row * cellHeightPx).toInt()

                val width = minOf(cellWidthPx.toInt(), resizedBitmap.width - startX)
                val height = minOf(cellHeightPx.toInt(), resizedBitmap.height - startY)

                val pieceBitmap = Bitmap.createBitmap(
                    resizedBitmap,
                    startX,
                    startY,
                    width,
                    height
                )

                val pieceUri =
                    saveBitmapToTemporaryFile(context, pieceBitmap, "piece_${row}_${col}")

                pieces.add(
                    DragItem(
                        title = "Piece ${row * nbCol + col + 1}",
                        content = "Drawable puzzle piece",
                        imageUri = pieceUri,
                        correctPosition = Position(row, col)
                    )
                )
            }
        }

        return pieces.shuffled()
    }

    private fun saveBitmapToTemporaryFile(context: Context, bitmap: Bitmap, name: String): String {
        val file = File(context.cacheDir, "${name}.png")
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }

    // Nettoyer les ressources lors de la destruction du ViewModel
    override fun onCleared() {
        super.onCleared()
        // Nettoyer les fichiers temporaires si nécessaire
        lastInitParams?.let { params ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val cacheDir = File(appContext.cacheDir.absolutePath)
                    cacheDir.listFiles()?.forEach { file ->
                        if (file.name.startsWith("piece_")) {
                            file.delete()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PuzzleViewModel", "Error cleaning temporary files", e)
                }
            }
        }
    }
}

// Classes de données pour le ViewModel
data class InitParams(
    val drawableResId: Int,
    val availableWidth: Dp,
    val availableHeight: Dp,
    val itemSpacing: Dp,
    val baseNbCol: Int
)

data class GridInfo(
    val nbCol: Int,
    val nbRow: Int,
    val cellWidth: Dp,
    val cellHeight: Dp
)

// États du puzzle
sealed class PuzzleState {
    object Idle : PuzzleState()
    object Loading : PuzzleState()
    data class Ready(
        val puzzlePieces: List<DragItem>,
        val gridInfo: GridInfo
    ) : PuzzleState()

    data class Error(val message: String) : PuzzleState()
}