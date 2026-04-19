package com.example.decisionmatrix.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Decision(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val status: String = "setup",        // "setup" | "scoring" | "done"
    val createdAt: Long = System.currentTimeMillis()
)