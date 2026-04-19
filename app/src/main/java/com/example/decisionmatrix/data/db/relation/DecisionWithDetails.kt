package com.example.decisionmatrix.data.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.decisionmatrix.data.model.Criterion
import com.example.decisionmatrix.data.model.Decision
import com.example.decisionmatrix.data.model.Option
import com.example.decisionmatrix.data.model.Score

data class DecisionWithDetails(

    @Embedded
    val decision: Decision,

    @Relation(
        parentColumn = "id",
        entityColumn = "decisionId"
    )
    val options: List<Option>,

    @Relation(
        parentColumn = "id",
        entityColumn = "decisionId"
    )
    val criteria: List<Criterion>,

    @Relation(
        parentColumn = "id",
        entityColumn = "decisionId"
    )
    val scores: List<Score>
)