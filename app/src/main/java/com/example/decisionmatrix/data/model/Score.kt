package com.example.decisionmatrix.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(Option::class, ["id"], ["optionId"], onDelete = CASCADE),
        ForeignKey(Criterion::class, ["id"], ["criterionId"], onDelete = CASCADE),
        ForeignKey(Decision::class, ["id"], ["decisionId"], onDelete = CASCADE)
    ],
    indices = [
        Index(value = ["optionId", "criterionId"], unique = true), // ✅ critical
        Index("decisionId")
    ]
)
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val decisionId: Long,
    val optionId: Long,
    val criterionId: Long,
    val value: Float
)