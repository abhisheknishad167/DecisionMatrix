package com.example.decisionmatrix.ui.adddecision

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionmatrix.data.model.Decision
import com.example.decisionmatrix.data.model.Option
import com.example.decisionmatrix.data.repository.DecisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AddDecisionUiState {
    object Idle : AddDecisionUiState
    object Saving : AddDecisionUiState
    data class Prefilled(val title: String, val optionNames: List<String>) : AddDecisionUiState
    data class NavigateTo(val decisionId: Long) : AddDecisionUiState
    data class Error(val message: String) : AddDecisionUiState
}

class AddDecisionViewModel(
    private val repository: DecisionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddDecisionUiState>(AddDecisionUiState.Idle)
    val uiState: StateFlow<AddDecisionUiState> = _uiState

    fun loadExisting(decisionId: Long) {
        viewModelScope.launch {
            try {
                repository.observeDecision(decisionId).collect { details ->
                    _uiState.value = AddDecisionUiState.Prefilled(
                        title       = details.decision.title,
                        optionNames = details.options.map { it.name }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AddDecisionUiState.Error(e.message ?: "Failed to load")
            }
        }
    }

    fun save(title: String, optionNames: List<String>, decisionId: Long = 0L) {
        if (title.isBlank()) {
            _uiState.value = AddDecisionUiState.Error("Title cannot be empty")
            return
        }
        if (optionNames.filter { it.isNotBlank() }.size < 2) {
            _uiState.value = AddDecisionUiState.Error("Add at least 2 options")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddDecisionUiState.Saving
            try {
                val cleanOptions = optionNames
                    .filter { it.isNotBlank() }
                    .map { it.trim() }

                if (decisionId == 0L) {
                    // Create new
                    val id = repository.insertDecision(Decision(title = title))
                    repository.insertOptions(
                        cleanOptions.map { Option(decisionId = id, name = it) }
                    )
                    _uiState.value = AddDecisionUiState.NavigateTo(id)
                } else {
                    // Update existing
                    repository.updateDecision(Decision(id = decisionId, title = title))
                    repository.replaceOptions(decisionId, cleanOptions)
                    _uiState.value = AddDecisionUiState.NavigateTo(decisionId)
                }

            } catch (e: Exception) {
                _uiState.value = AddDecisionUiState.Error(e.message ?: "Error saving")
            }
        }
    }
}