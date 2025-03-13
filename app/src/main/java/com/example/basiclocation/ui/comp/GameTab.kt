package com.example.basiclocation.ui.comp

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.basiclocation.model.DragItem
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


@Composable
fun GameTab(poiId: String) {

    var items by remember {
        mutableStateOf(
            listOf(
                DragItem("invisible_header", ""), // élément invisible
                DragItem("Item 1", "Content 1"),
                DragItem("Item 2", "Content 2"),
                DragItem("Item 3", "Content 3")
            )
        )
    }

    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            // Ignorer les déplacements impliquant l'élément invisible
            if (from.index != 0 && to.index != 0) {
                val fromIndex = from.index
                val toIndex = to.index
                val newList = items.toMutableList()
                newList.add(toIndex, newList.removeAt(fromIndex))
                items = newList
            }
        }
    )

    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .reorderable(state)
    ) {
        itemsIndexed(
            items,
            key = { _, item -> item.title }
        ) { index, item ->
            ReorderableItem(state, key = item.title) { isDragging ->
                // Ne rendre l'item que s'il n'est pas l'élément invisible
                if (index != 0) {
                    DraggableItem(
                        item = item,
                        modifier = Modifier.detectReorder(state)
                    )
                } else {
                    Spacer(modifier = Modifier.height(0.dp))
                }
            }
        }
    }
}