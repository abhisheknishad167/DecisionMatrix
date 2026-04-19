package com.example.decisionmatrix.ui.criteria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionmatrix.data.model.Criterion
import com.example.decisionmatrix.data.repository.DecisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface CriteriaUiState {
    object Loading : CriteriaUiState
    data class Ready(
        val criteria: List<Criterion>,
        val navigateToScoring: Boolean = false   // ← navigation trigger
    ) : CriteriaUiState
    data class Error(val message: String) : CriteriaUiState
}

class CriteriaViewModel(
    private val repository: DecisionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CriteriaUiState>(CriteriaUiState.Loading)
    val uiState: StateFlow<CriteriaUiState> = _uiState

    private var decisionId: Long = 0L

    fun load(id: Long) {
        decisionId = id
        viewModelScope.launch {
            try {
                repository.observeDecision(id).collect { details ->
                    val current = _uiState.value
                    _uiState.value = CriteriaUiState.Ready(
                        criteria = details.criteria,
                        // preserve navigate flag if already set
                        navigateToScoring = (current as? CriteriaUiState.Ready)
                            ?.navigateToScoring ?: false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CriteriaUiState.Error(e.message ?: "Failed to load")
            }
        }
    }

    fun addCriterion(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCriteria(
                listOf(Criterion(decisionId = decisionId, name = name, weight = 1f))
            )
        }
    }

    fun updateWeight(criterion: Criterion, weight: Float) {
        viewModelScope.launch {
            repository.updateCriterion(criterion.copy(weight = weight))
        }
    }

    fun onNextClicked() {
        val current = _uiState.value as? CriteriaUiState.Ready ?: return
        if (current.criteria.isEmpty()) {
            _uiState.value = CriteriaUiState.Error("Add at least one criterion")
            return
        }
        _uiState.value = current.copy(navigateToScoring = true)
    }

    fun onNavigationHandled() {
        val current = _uiState.value as? CriteriaUiState.Ready ?: return
        _uiState.value = current.copy(navigateToScoring = false)
    }
}