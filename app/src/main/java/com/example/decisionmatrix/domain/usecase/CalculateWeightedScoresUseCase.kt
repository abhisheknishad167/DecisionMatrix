package com.example.decisionmatrix.domain.usecase

import com.example.decisionmatrix.data.model.Option
import com.example.decisionmatrix.data.model.Criterion
import com.example.decisionmatrix.data.model.Score
import com.example.decisionmatrix.domain.model.RankedOption

class CalculateWeightedScoresUseCase {

    companion object {
        const val DEFAULT_SCORE = 5f
    }

    fun execute(
        options: List<Option>,
        criteria: List<Criterion>,
        scores: List<Score>
    ): List<RankedOption> {

        val scoreMap = scores.associate {
            (it.optionId to it.criterionId) to it.value
        }

        return options.map { option ->

            val breakdown = criteria.associateWith { criterion ->
                val raw = scoreMap[option.id to criterion.id] ?: DEFAULT_SCORE
                criterion.weight * raw
            }

            RankedOption(
                option = option,
                totalScore = breakdown.values.sum(),
                breakdown = breakdown
            )
        }.sortedByDescending { it.totalScore }
    }
}