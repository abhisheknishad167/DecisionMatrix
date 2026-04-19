package com.example.decisionmatrix.domain.model

import com.example.decisionmatrix.data.model.Option
import com.example.decisionmatrix.data.model.Criterion

data class RankedOption(
    val option: Option,
    val totalScore: Float,
    val breakdown: Map<Criterion, Float>
)
