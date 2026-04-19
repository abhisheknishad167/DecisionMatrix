package com.example.decisionmatrix.ui.adddecision

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decisionmatrix.data.repository.DecisionRepository

class AddDecisionViewModelFactory(
    private val repository: DecisionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddDecisionViewModel(repository) as T
    }
}