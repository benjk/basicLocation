package com.example.basiclocation.ui.comp

import android.content.Context
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
import androidx.compose.ui.unit.dp
import com.example.basiclocation.model.DragItem


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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        ReorderableGrid(
            initialItems = items,
            nbCol = 3,
            cellSize = 80.dp
        )
    }
}