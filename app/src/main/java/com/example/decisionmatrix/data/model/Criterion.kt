package com.example.decisionmatrix.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Criterion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val decisionId: Long,
    val name: String,
    val weight: Float        // raw user input (1–10), normalized later
)