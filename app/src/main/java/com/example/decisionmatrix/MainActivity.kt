package com.example.decisionmatrix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.decisionmatrix.ui.adddecision.AddDecisionScreen
import com.example.decisionmatrix.ui.criteria.CriteriaScreen
import com.example.decisionmatrix.ui.decisionlist.DecisionListScreen
import com.example.decisionmatrix.ui.results.ResultsScreen
import com.example.decisionmatrix.ui.scoring.ScoringScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "decision_list"
            ) {

                // Decision list — now includes onEdit
                composable("decision_list") {
                    DecisionListScreen(
                        onNew  = { navController.navigate("add_decision/0") },
                        onOpen = { id -> navController.navigate("scoring/$id") },
                        onEdit = { id -> navController.navigate("add_decision/$id") }
                    )
                }

                // Add / edit decision — 0 means new, real id means edit
                composable("add_decision/{decisionId}") { backStack ->
                    val decisionId = backStack.arguments
                        ?.getString("decisionId")
                        ?.toLongOrNull() ?: 0L

                    AddDecisionScreen(
                        decisionId = decisionId,
                        onSaved = { id ->
                            navController.navigate("criteria/$id") {
                                popUpTo("add_decision/$decisionId") { inclusive = true }
                            }
                        }
                    )
                }

                // Criteria
                composable("criteria/{decisionId}") { backStack ->
                    val id = backStack.arguments
                        ?.getString("decisionId")
                        ?.toLongOrNull() ?: return@composable

                    CriteriaScreen(
                        decisionId = id,
                        onNext = { navController.navigate("scoring/$id") }
                    )
                }

                // Scoring
                composable("scoring/{decisionId}") { backStack ->
                    val id = backStack.arguments
                        ?.getString("decisionId")
                        ?.toLongOrNull() ?: return@composable

                    ScoringScreen(
                        decisionId = id,
                        onResults  = { navController.navigate("results/$id") }
                    )
                }

                // Results
                composable("results/{decisionId}") { backStack ->
                    val id = backStack.arguments
                        ?.getString("decisionId")
                        ?.toLongOrNull() ?: return@composable

                    ResultsScreen(decisionId = id,onDone     = {
                        navController.navigate("decision_list") {
                            popUpTo("decision_list") { inclusive = true }  // clears entire back stack
                        }
                    })
                }
            }
        }
    }
}