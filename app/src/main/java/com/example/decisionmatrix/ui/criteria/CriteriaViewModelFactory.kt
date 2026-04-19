package com.example.decisionmatrix.ui.criteria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decisionmatrix.data.repository.DecisionRepository

class CriteriaViewModelFactory(
    private val repository: DecisionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CriteriaViewModel(repository) as T
    }
}