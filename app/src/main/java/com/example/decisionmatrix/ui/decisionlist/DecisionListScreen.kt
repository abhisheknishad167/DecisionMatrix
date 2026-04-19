package com.example.decisionmatrix.ui.decisionlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionmatrix.DecisionMatrixApp
import com.example.decisionmatrix.data.db.relation.DecisionWithDetails

@Composable
fun DecisionListScreen(
    onNew: () -> Unit = {},
    onOpen: (Long) -> Unit = {},
    onEdit: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as DecisionMatrixApp

    val viewModel: DecisionListViewModel = viewModel(
        factory = DecisionListViewModelFactory(app.container.decisionRepository)
    )

    val decisions by viewModel.decisions.collectAsState()

    // Tracks which decision is pending delete confirmation
    var pendingDelete by remember { mutableStateOf<DecisionWithDetails?>(null) }

    LaunchedEffect(Unit) { viewModel.load() }

    // Delete confirmation dialog
    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete decision?") },
            text  = { Text("\"${item.decision.title}\" and all its data will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(item)
                    pendingDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNew) {
                Text("+")
            }
        }
    ) { padding ->

        if (decisions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No decisions yet. Tap + to create one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = decisions,
                    key   = { it.decision.id }   // stable keys prevent flicker on delete
                ) { item ->
                    DecisionItem(
                        decision  = item,
                        onClick   = { onOpen(item.decision.id) },
                        onEdit    = { onEdit(item.decision.id) },
                        onDelete  = { pendingDelete = item }
                    )
                }
            }
        }
    }
}

@Composable
fun DecisionItem(
    decision: DecisionWithDetails,
    onClick:  () -> Unit,
    onEdit:   () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick  = onClick
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 10.dp)) {

            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = decision.decision.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // Status chip
                val (chipText, chipBg, chipFg) = when (decision.decision.status) {
                    "done"    -> Triple("done",    Color(0xFFE1F5EE), Color(0xFF085041))
                    "scoring" -> Triple("scoring", Color(0xFFFAEEDA), Color(0xFF633806))
                    else      -> Triple("setup",   Color(0xFFEEEDFE), Color(0xFF3C3489))
                }
                Surface(
                    color = chipBg,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text     = chipText,
                        color    = chipFg,
                        style    = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Meta row
            Text(
                text  = "${decision.options.size} options · ${decision.criteria.size} criteria",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Action buttons
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = onEdit,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Edit")
                }
                TextButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint     = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}