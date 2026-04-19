package com.example.decisionmatrix.ui.scoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionmatrix.data.model.Criterion
import com.example.decisionmatrix.data.model.Option
import com.example.decisionmatrix.data.model.Score
import com.example.decisionmatrix.data.repository.DecisionRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ScoringUiState {
    object Loading : ScoringUiState

    data class Ready(
        val decisionId: Long,
        val options: List<Option>,
        val criteria: List<Criterion>,
        val scores: Map<Pair<Long, Long>, Float>,
        val selectedOptionIndex: Int = 0
    ) : ScoringUiState

    data class Error(val message: String) : ScoringUiState
}

class ScoringViewModel(
    private val repo: DecisionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScoringUiState>(ScoringUiState.Loading)
    val uiState: StateFlow<ScoringUiState> = _uiState

    private val scoreBuffer = MutableSharedFlow<Score>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private var pendingScore: Score? = null

    init {
        viewModelScope.launch {
            scoreBuffer
                .debounce(300)
                .collect { score ->
                    repo.upsertScore(score)
                    pendingScore = null
                }
        }
    }

    fun load(decisionId: Long) {
        viewModelScope.launch {
            try {
                repo.observeDecision(decisionId).collect { details ->
                    val current = _uiState.value as? ScoringUiState.Ready

                    _uiState.value = ScoringUiState.Ready(
                        decisionId = decisionId,
                        options    = details.options,
                        criteria   = details.criteria,
                        scores     = details.scores.associate {
                            (it.optionId to it.criterionId) to it.value
                        },
                        // ← preserve the selected index across DB emissions
                        selectedOptionIndex = current?.selectedOptionIndex ?: 0
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ScoringUiState.Error(e.message ?: "Failed to load")
            }
        }
    }

    fun updateScore(optionId: Long, criterionId: Long, value: Float) {
        val current = _uiState.value as? ScoringUiState.Ready ?: return

        // Round to nearest integer to avoid float drift
        val rounded = Math.round(value).toFloat()

        _uiState.value = current.copy(
            scores = current.scores + ((optionId to criterionId) to rounded)
        )

        val score = Score(
            decisionId  = current.decisionId,
            optionId    = optionId,
            criterionId = criterionId,
            value       = rounded
        )
        pendingScore = score
        scoreBuffer.tryEmit(score)
    }

    fun selectOption(index: Int) {
        val current = _uiState.value as? ScoringUiState.Ready ?: return
        _uiState.value = current.copy(selectedOptionIndex = index)
    }

    fun onNavigateToResults(onReady: (Long) -> Unit) {
        viewModelScope.launch {
            pendingScore?.let {
                repo.upsertScore(it)
                pendingScore = null
            }
            val id = (_uiState.value as? ScoringUiState.Ready)?.decisionId ?: return@launch
            onReady(id)
        }
    }
}