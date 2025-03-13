package com.example.basiclocation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DragItem(
    val title: String,
    val content: String
) : Parcelable