package com.example.basiclocation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DragItem(
    val title: String,
    val content: String,
    val imageUri: String? = null,
    val correctPosition: Position? = null  // Ajouté pour vérifier la résolution
) : Parcelable

@Parcelize
data class Position(val row: Int, val col: Int) : Parcelable

