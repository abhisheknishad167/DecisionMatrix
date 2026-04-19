package com.example.decisionmatrix.ui.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decisionmatrix.data.repository.DecisionRepository

class ResultsViewModelFactory(
    private val repository: DecisionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ResultsViewModel(repository) as T
    }
}