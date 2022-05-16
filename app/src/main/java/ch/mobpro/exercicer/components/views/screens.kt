package ch.mobpro.exercicer.components.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.mobpro.exercicer.R
import ch.mobpro.exercicer.components.views.admin.AdminNavigationController
import ch.mobpro.exercicer.components.views.goals.GoalCard
import ch.mobpro.exercicer.components.views.goals.GoalsPage
import ch.mobpro.exercicer.components.views.reporting.ReportingPage
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.viewmodel.ReportingViewModel
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import java.time.LocalDate

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
fun ScreenTitle(title: String) {
    Text(
        title,
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp),
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ScreenController(navController: NavHostController) {
    NavHost(navController, startDestination = Route.ROUTE_TRAINING.route) {

        composable(Route.ROUTE_TRAINING.route) {
            TrainingPage()
        }

        composable(Route.ROUTE_GOAL.route) {
            GoalsPage()
        }

        composable(Route.ROUTE_REPORTING.route) {
            ReportingPage()
        }

        composable(Route.ROUTE_ADMIN.route) {
            AdminNavigationController()
        }
    }
}
