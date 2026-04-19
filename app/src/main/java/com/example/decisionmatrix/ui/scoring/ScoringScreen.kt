package com.example.decisionmatrix.ui.scoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionmatrix.DecisionMatrixApp
import com.example.decisionmatrix.data.model.Criterion

@Composable
fun ScoringScreen(
    decisionId: Long,
    onResults: (Long) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as DecisionMatrixApp

    val viewModel: ScoringViewModel = viewModel(
        factory = ScoringViewModelFactory(app.container.decisionRepository)
    )

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load(decisionId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (state) {
            is ScoringUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ScoringUiState.Error -> {
                Text((state as ScoringUiState.Error).message)
            }

            is ScoringUiState.Ready -> {
                val data = state as ScoringUiState.Ready

                if (data.options.isEmpty() || data.criteria.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    return@Column
                }

                val selectedOption = data.options[data.selectedOptionIndex]

                Text(
                    "Scoring: ${selectedOption.name}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(16.dp))

                // Option selector chips
                Row(modifier = Modifier.fillMaxWidth()) {
                    data.options.forEachIndexed { index, option ->
                        FilterChip(
                            selected = index == data.selectedOptionIndex,
                            onClick  = { viewModel.selectOption(index) },
                            label    = { Text(option.name) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(
                        items = data.criteria,
                        key   = { it.id }   // stable key — prevents sliders recomposing each other
                    ) { criterion ->
                        ScoreSliderItem(
                            criterion     = criterion,
                            savedValue    = data.scores[selectedOption.id to criterion.id] ?: 5f,
                            onValueCommit = { value ->
                                viewModel.updateScore(selectedOption.id, criterion.id, value)
                            }
                        )
                        HorizontalDivider()
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick  = { viewModel.onNavigateToResults { id -> onResults(id) } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("See results")
                }
            }
        }
    }
}

@Composable
fun ScoreSliderItem(
    criterion:     Criterion,
    savedValue:    Float,
    onValueCommit: (Float) -> Unit
) {
    var localValue by remember(criterion.id) { mutableStateOf(savedValue) }

    LaunchedEffect(savedValue) {
        localValue = savedValue
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(criterion.name)
            Text("${localValue.toInt()} / 10")
        }

        Slider(
            value         = localValue,
            onValueChange = { localValue = it },          // only updates local — no DB write mid-drag
            onValueChangeFinished = {
                val rounded = Math.round(localValue).toFloat()
                localValue = rounded
                onValueCommit(rounded)                    // DB write only on finger lift
            },
            valueRange = 0f..10f,
            steps      = 9
        )
    }
}