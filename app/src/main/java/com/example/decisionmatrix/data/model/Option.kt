package com.example.decisionmatrix.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Option(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val decisionId: Long,
    val name: String
)