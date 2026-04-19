package com.example.decisionmatrix.ui.decisionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionmatrix.data.db.relation.DecisionWithDetails
import com.example.decisionmatrix.data.repository.DecisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DecisionListViewModel(
    private val repository: DecisionRepository
) : ViewModel() {

    private val _decisions = MutableStateFlow<List<DecisionWithDetails>>(emptyList())
    val decisions: StateFlow<List<DecisionWithDetails>> = _decisions

    fun load() {
        viewModelScope.launch {
            repository.observeAllDecisions().collect {
                _decisions.value = it
            }
        }
    }

    fun delete(item: DecisionWithDetails) {
        viewModelScope.launch {
            repository.deleteDecision(item.decision)
        }
    }
}