package com.example.decisionmatrix.ui.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionmatrix.data.repository.DecisionRepository
import com.example.decisionmatrix.domain.model.RankedOption
import com.example.decisionmatrix.domain.usecase.CalculateWeightedScoresUseCase
import com.example.decisionmatrix.domain.usecase.NormaliseWeightsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ResultsUiState {
    object Loading : ResultsUiState

    data class Ready(
        val ranked: List<RankedOption>,
        val usedDefaults: Boolean
    ) : ResultsUiState

    data class Error(val message: String) : ResultsUiState
}

class ResultsViewModel(
    private val repo: DecisionRepository
) : ViewModel() {

    private val calculate = CalculateWeightedScoresUseCase()
    private val normalise = NormaliseWeightsUseCase()

    private val _uiState = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val uiState: StateFlow<ResultsUiState> = _uiState

    fun load(decisionId: Long) {
        viewModelScope.launch {
            try {
                repo.observeDecision(decisionId).collect { details ->

                    val normalised = normalise.execute(details.criteria)

                    val ranked = calculate.execute(
                        options = details.options,
                        criteria = normalised,
                        scores = details.scores
                    )

                    val scoredPairs = details.scores.map {
                        it.optionId to it.criterionId
                    }.toSet()

                    val usedDefaults = details.options.any { o ->
                        details.criteria.any { c ->
                            (o.id to c.id) !in scoredPairs
                        }
                    }

                    _uiState.value = ResultsUiState.Ready(
                        ranked = ranked,
                        usedDefaults = usedDefaults
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ResultsUiState.Error(e.message ?: "Error")
            }
        }
    }
}