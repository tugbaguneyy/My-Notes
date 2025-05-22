package com.example.mynotes.domain.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.Date

@IgnoreExtraProperties
data class Note(
    var id: String? = "",
    var userId: String? = "",
    var title: String? = "",
    var description: String? = "",
    val date: Date? = Date(),
    var isFavorite: Boolean = false,
    var isDeleted: Boolean = false
)