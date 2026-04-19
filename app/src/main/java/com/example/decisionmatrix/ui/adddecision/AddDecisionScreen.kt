package com.example.decisionmatrix.ui.adddecision

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionmatrix.DecisionMatrixApp

@Composable
fun AddDecisionScreen(
    decisionId: Long = 0L,          // ← added: 0 = new, real id = edit
    onSaved: (Long) -> Unit,
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as DecisionMatrixApp

    val viewModel: AddDecisionViewModel = viewModel(
        factory = AddDecisionViewModelFactory(app.container.decisionRepository)
    )

    var title by remember { mutableStateOf("") }
    var optionNames by remember { mutableStateOf(listOf("", "")) }

    val state by viewModel.uiState.collectAsState()

    // Pre-fill if editing
    LaunchedEffect(decisionId) {
        if (decisionId != 0L) {
            viewModel.loadExisting(decisionId)
        }
    }

    // Sync pre-filled data into local state
    LaunchedEffect(state) {
        when (val s = state) {
            is AddDecisionUiState.Prefilled -> {
                title = s.title
                optionNames = s.optionNames
            }
            is AddDecisionUiState.NavigateTo ->
                onSaved(s.decisionId)
            is AddDecisionUiState.Error ->
                onError(s.message)
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            if (decisionId == 0L) "Create decision" else "Edit decision",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Decision title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Text("Options", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))

        optionNames.forEachIndexed { index, name ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { newVal ->
                        optionNames = optionNames.toMutableList().also { it[index] = newVal }
                    },
                    label = { Text("Option ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                if (optionNames.size > 2) {
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = {
                        optionNames = optionNames.toMutableList().also { it.removeAt(index) }
                    }) {
                        Text("✕")
                    }
                }
            }
        }

        TextButton(onClick = { optionNames = optionNames + "" }) {
            Text("+ add option")
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.save(title, optionNames, decisionId) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is AddDecisionUiState.Saving
        ) {
            if (state is AddDecisionUiState.Saving) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
            } else {
                Text(if (decisionId == 0L) "Next: add criteria" else "Save changes")
            }
        }
    }
}