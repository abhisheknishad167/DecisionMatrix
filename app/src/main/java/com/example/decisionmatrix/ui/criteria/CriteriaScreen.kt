package com.example.decisionmatrix.ui.criteria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionmatrix.DecisionMatrixApp
import com.example.decisionmatrix.data.model.Criterion

@Composable
fun CriteriaScreen(
    decisionId: Long,
    onNext: (Long) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as DecisionMatrixApp

    val viewModel: CriteriaViewModel = viewModel(
        factory = CriteriaViewModelFactory(app.container.decisionRepository)
    )

    val state by viewModel.uiState.collectAsState()
    var newCriterion by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.load(decisionId)
    }

    // Handle navigation
    LaunchedEffect(state) {
        val s = state
        if (s is CriteriaUiState.Ready && s.navigateToScoring) {
            viewModel.onNavigationHandled()
            onNext(decisionId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Add criteria", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        // Add criterion row
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = newCriterion,
                onValueChange = { newCriterion = it },
                label = { Text("Criterion name") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                viewModel.addCriterion(newCriterion)
                newCriterion = ""
            }) {
                Text("+")
            }
        }

        Spacer(Modifier.height(16.dp))

        when (val s = state) {
            is CriteriaUiState.Loading -> CircularProgressIndicator()

            is CriteriaUiState.Error -> Text(
                s.message,
                color = MaterialTheme.colorScheme.error
            )

            is CriteriaUiState.Ready -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(s.criteria) { item ->
                        CriterionItem(
                            criterion = item,
                            onWeightChange = { weight ->
                                viewModel.updateWeight(item, weight)
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.onNextClicked() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next: score options")
        }
    }
}

@Composable
fun CriterionItem(
    criterion: Criterion,
    onWeightChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(criterion.name)
            Text("${criterion.weight.toInt()} / 10")
        }
        Slider(
            value = criterion.weight,
            onValueChange = onWeightChange,
            valueRange = 1f..10f,
            steps = 8
        )
    }
}