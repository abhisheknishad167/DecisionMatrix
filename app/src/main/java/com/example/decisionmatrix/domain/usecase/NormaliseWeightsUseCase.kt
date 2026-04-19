package com.example.decisionmatrix.domain.usecase

import com.example.decisionmatrix.data.model.Criterion

class NormaliseWeightsUseCase {

    fun execute(criteria: List<Criterion>): List<Criterion> {
        val total = criteria.sumOf { it.weight.toDouble() }.toFloat()
        if (total == 0f) return criteria

        return criteria.map {
            it.copy(weight = it.weight / total)
        }
    }
}