package com.example.basiclocation.ui.comp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.basiclocation.model.DragItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun PuzzleGrid(
    drawableResId: Int,
    availableWidth: Dp,
    availableHeight: Dp,
    itemSpacing: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val puzzlePieces = remember { mutableStateOf<List<DragItem>>(emptyList()) }
    val gridInfo = remember { mutableStateOf<GridInfo?>(null) }
    val density = LocalDensity.current

    LaunchedEffect(drawableResId, availableWidth, availableHeight, itemSpacing) {
        withContext(Dispatchers.IO) {
            // 1. Charger l'image et obtenir ses dimensions
            val bitmap = loadBitmap(context, drawableResId)
            if (bitmap != null) {

                // 3. Calculer la largeur disponible et la hauteur disponible
                val availableWidthPx = with(density) { availableWidth.toPx() }
                val availableHeightPx = with(density) { availableHeight.toPx() }
                val spacingPx = with(density) { itemSpacing.toPx() }

                // 4. Déterminer le nombre de colonnes en fonction de l'orientation de l'image
                val imageRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                val nbCol = when {
                    imageRatio > 1 -> 5  // Paysage
                    else -> 3  // Portrait
                }

                // 5. Calculer la largeur et la hauteur de la grille en fonction du ratio de l'image
                val (gridWidthPx, gridHeightPx) = if (imageRatio > 1) {
                    // L'image est plus large que haute (paysage), on fixe la largeur
                    val gridWidth = availableWidthPx
                    val gridHeight =
                        gridWidth / imageRatio  // Calculer la hauteur pour respecter le ratio
                    gridWidth to gridHeight
                } else {
                    // L'image est plus haute que large (portrait), on fixe la hauteur
                    val gridHeight = availableHeightPx
                    val gridWidth =
                        gridHeight * imageRatio  // Calculer la largeur pour respecter le ratio
                    gridWidth to gridHeight
                }

                Log.d("ZZZ", "gridHeightPx : " + gridHeightPx)


                // 6. Calculer la taille des cellules pour remplir la grille
                val cellWidthPx = (gridWidthPx - (nbCol + 1) * spacingPx) / nbCol
                var cellHeightPx = cellWidthPx
                // Calculer le nombre de lignes (nbRow) nécessaire pour que la grille occupe exactement gridHeightPx
                val nbRow = ((gridHeightPx) / (cellHeightPx )).toInt()

                // Recalculer la hauteur des cellules pour que la multiplication par nbRow donne exactement gridHeightPx
                cellHeightPx = (gridHeightPx - ((nbRow + 1) * spacingPx)) / nbRow

                // 4. Créer les pièces du puzzle
                puzzlePieces.value = splitImageIntoPuzzlePieces(
                    context,
                    bitmap,
                    nbCol,
                    nbRow,
                    cellWidthPx,
                    cellHeightPx,
                    spacingPx
                )

                // 5. Stocker les informations de la grille
                val cellWidthDp = with(density) { cellWidthPx.toDp() }
                val cellHeightDp = with(density) { cellHeightPx.toDp() }
                gridInfo.value = GridInfo(nbCol, nbRow, cellWidthDp, cellHeightDp)
            }
        }
    }

    gridInfo.value?.let { info ->
        if (puzzlePieces.value.isNotEmpty()) {
            ReorderableGrid(
                initialItems = puzzlePieces.value,
                cellWidth = info.cellWidth,
                cellHeight = info.cellHeight,
                nbCol = info.nbCol,
                nbRow = info.nbRow,
                itemSpacing = itemSpacing,
                modifier = modifier
            )
        }
    }
}

private suspend fun splitImageIntoPuzzlePieces(
    context: Context,
    bitmap: Bitmap,
    nbCol: Int,
    nbRow: Int,
    cellWidthPx: Float,
    cellHeightPx: Float,
    spacingPx: Float
): List<DragItem> {
    val pieces = mutableListOf<DragItem>()

    // 1. Redimensionner le bitmap pour qu'il tienne dans la grille calculée
    val bitmapHeight = cellHeightPx * nbRow
    val bitmapWidth = cellWidthPx * nbCol;
    Log.d("ZZZ", "ZA SPACE " + spacingPx)
    Log.d("ZZZ", "ZA " + bitmapHeight)
    Log.d("ZZZ", "ZAINT " + bitmapHeight.toInt())
    val resizedBitmap = Bitmap.createScaledBitmap(
        bitmap,
        bitmapWidth.toInt(),
        bitmapHeight.toInt(),
        true
    )

    // 2. Découper l'image redimensionnée en plusieurs pièces
    for (row in 0 until nbRow) {
        for (col in 0 until nbCol) {
            // Calculer les coordonnées de découpe pour chaque pièce
            val startX = (col * cellWidthPx).toInt()
            val startY = (row * cellHeightPx).toInt()

            // Vérifier que la découpe ne dépasse pas les bords du bitmap
            val width = minOf(cellWidthPx.toInt(), resizedBitmap.width - startX)
            val height = minOf(cellHeightPx.toInt(), resizedBitmap.height - startY)

            // Découper la pièce de puzzle
            val pieceBitmap = Bitmap.createBitmap(
                resizedBitmap,
                startX,
                startY,
                width,
                height
            )

            // Sauvegarder la pièce dans un fichier temporaire
            val pieceUri = saveBitmapToTemporaryFile(context, pieceBitmap, "piece_${row}_${col}")

            pieces.add(
                DragItem(
                    title = "Piece ${row * nbCol + col + 1}",
                    content = "Drawable puzzle piece",
                    imageUri = pieceUri
                )
            )
        }
    }

    return pieces
}


private fun loadBitmap(context: Context, drawableResId: Int): Bitmap? {
    return try {
        BitmapFactory.decodeResource(context.resources, drawableResId)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
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

data class GridInfo(
    val nbCol: Int,
    val nbRow: Int,
    val cellWidth: Dp,
    val cellHeight: Dp
)