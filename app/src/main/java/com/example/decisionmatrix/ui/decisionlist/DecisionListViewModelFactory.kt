package com.example.decisionmatrix.ui.decisionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decisionmatrix.data.repository.DecisionRepository

class DecisionListViewModelFactory(
    private val repository: DecisionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DecisionListViewModel(repository) as T
    }
}