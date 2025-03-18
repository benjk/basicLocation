package com.example.basiclocation.ui.comp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import com.example.basiclocation.model.DragItem
import com.example.basiclocation.ui.theme.primaryColor
import com.example.basiclocation.ui.theme.secondaryColor
import com.example.basiclocation.ui.theme.thirdColor
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun ReorderableGrid(
    initialItems: List<DragItem>,
    cellSize: Dp,
    nbCol: Int,
    modifier: Modifier = Modifier
) {
    var items by remember { mutableStateOf(initialItems) }

    val view = LocalView.current
    val lazyGridState = rememberLazyGridState()
    val reorderableLazyGridState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        items = items.toMutableList().apply {
            val temp = this[from.index]
            this[from.index] = this[to.index]
            this[to.index] = temp
        }
        ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK)
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(primaryColor)
    ) {
        val itemSpacing = 4.dp
        val gridWidth = cellSize * nbCol + itemSpacing * (nbCol - 1);

        LazyVerticalGrid(
            columns = GridCells.Fixed(nbCol),
            modifier = Modifier
                .width(gridWidth)
                .background(secondaryColor),
            state = lazyGridState,
            contentPadding = PaddingValues(itemSpacing),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            itemsIndexed(items, key = { _, item -> item.title }) { index, item ->
                ReorderableItem(reorderableLazyGridState, key = item.title) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                    Surface(
                        shadowElevation = elevation,
                        modifier = Modifier
                            .size(cellSize)
                            .draggableHandle(
                                onDragStarted = {
                                    ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_START)
                                },
                                onDragStopped = {
                                    ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_END)
                                },
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().background(thirdColor),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(item.title)
                        }
                    }
                }
            }
        }
    }

}
