package com.example.decisionmatrix.ui.results

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionmatrix.DecisionMatrixApp
import com.example.decisionmatrix.domain.model.RankedOption
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun ResultsScreen(
    decisionId: Long,
    onDone: () -> Unit = {}          // ← added
) {
    val context = LocalContext.current
    val app = context.applicationContext as DecisionMatrixApp

    val viewModel: ResultsViewModel = viewModel {
        ResultsViewModel(app.container.decisionRepository)
    }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load(decisionId) }

    when (state) {
        is ResultsUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ResultsUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text((state as ResultsUiState.Error).message)
            }
        }
        is ResultsUiState.Ready -> {
            val data = state as ResultsUiState.Ready
            ResultsContent(
                ranked      = data.ranked,
                usedDefaults = data.usedDefaults,
                onDone      = onDone
            )
        }
    }
}

@Composable
private fun ResultsContent(
    ranked:       List<RankedOption>,
    usedDefaults: Boolean,
    onDone:       () -> Unit
) {
    val model = remember(ranked) {
        entryModelOf(*ranked.map { it.totalScore }.toTypedArray())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Results", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible = usedDefaults) {
            Card(
                colors   = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    "Some scores used default value of 5",
                    modifier = Modifier.padding(12.dp),
                    style    = MaterialTheme.typography.bodySmall
                )
            }
        }

        Chart(
            chart       = columnChart(),
            model       = model,
            startAxis   = rememberStartAxis(),
            bottomAxis  = rememberBottomAxis(
                valueFormatter = { value, _ ->
                    ranked.getOrNull(value.toInt())?.option?.name ?: ""
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ranked) { item ->
                RankedItem(
                    item     = item,
                    isWinner = ranked.indexOf(item) == 0
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Done button — goes back to decision list
        Button(
            onClick  = onDone,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF534AB7)
            )
        ) {
            Text("Done", color = Color.White)
        }
    }
}

@Composable
private fun RankedItem(item: RankedOption, isWinner: Boolean) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick  = { expanded = !expanded },
        colors   = CardDefaults.cardColors(
            containerColor = if (isWinner) Color(0xFFEEEDFE)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isWinner) BorderStroke(1.5.dp, Color(0xFF534AB7)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.option.name, style = MaterialTheme.typography.titleMedium)
                if (isWinner) {
                    Surface(color = Color(0xFFE1F5EE), shape = MaterialTheme.shapes.small) {
                        Text(
                            "top pick",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            color    = Color(0xFF085041)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                "Score: ${"%.2f".format(item.totalScore)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isWinner) Color(0xFF3C3489)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress    = { (item.totalScore / 10f).coerceIn(0f, 1f) },
                modifier    = Modifier.fillMaxWidth().height(6.dp),
                color       = if (isWinner) Color(0xFF7F77DD) else Color(0xFF1D9E75),
                trackColor  = Color(0xFFE0E0E0)
            )

            AnimatedVisibility(
                visible = expanded,
                enter   = fadeIn(tween(300)) + expandVertically(),
                exit    = fadeOut(tween(200)) + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Breakdown",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    item.breakdown.forEach { (criterion, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                criterion.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "%.2f".format(value),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                if (expanded) "hide breakdown ▲" else "show breakdown ▼",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}