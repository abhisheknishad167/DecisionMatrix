package com.example.decisionmatrix.ui.scoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decisionmatrix.data.repository.DecisionRepository

class ScoringViewModelFactory(
    private val repository: DecisionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScoringViewModel(repository) as T
    }
}