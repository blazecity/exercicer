package ch.mobpro.exercicer.components.views

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.mobpro.exercicer.R
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import javax.inject.Inject

enum class Route(val route: String) {
    ROUTE_TRAINING("r_training"),
    ROUTE_GOAL("r_goal"),
    ROUTE_REPORTING("r_reporting"),
    ROUTE_ADMIN("r_admin")
}

sealed class Screens(val route: Route, val label: String, val icon: Int) {
    object Training: Screens(Route.ROUTE_TRAINING, "Training", R.drawable.running)
    object Goal: Screens(Route.ROUTE_GOAL, "Ziele", R.drawable.flag)
    object Reporting: Screens(Route.ROUTE_REPORTING, "Reporting", R.drawable.reporting)
    object Admin: Screens(Route.ROUTE_ADMIN, "Admin", R.drawable.settings)

    object Items {
        val items = listOf(
            Training, Goal, Reporting, Admin
        )
    }
}

@Composable
fun ScreenController(navController: NavHostController) {
    val reportingViewModel: TrainingViewModel = hiltViewModel()

    NavHost(navController, startDestination = Route.ROUTE_TRAINING.route) {

        composable(Route.ROUTE_TRAINING.route) {
            Text("Training")
        }

        composable(Route.ROUTE_GOAL.route) {
            Text("Goal")
        }

        composable(Route.ROUTE_REPORTING.route) {
            ReportingPage(reportingViewModel = reportingViewModel)
        }

        composable(Route.ROUTE_ADMIN.route) {
            Text("Admin")
        }
    }
}
