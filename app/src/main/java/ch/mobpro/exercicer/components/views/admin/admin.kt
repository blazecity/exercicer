package ch.mobpro.exercicer.components.views.admin

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.mobpro.exercicer.R
import ch.mobpro.exercicer.components.*
import ch.mobpro.exercicer.components.views.ScreenTitle
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.viewmodel.SportViewModel
import ch.mobpro.exercicer.viewmodel.TrainingTypeViewModel


enum class AdminRoute(val route: String) {
    OVERVIEW("overview"),
    TRAINING_TYPES("admin_training_types"),
    SPORTS("admin_sports")
}

@Composable
fun AdminNavigationController() {
    val navController = rememberNavController()
    val trainingTypeViewModel: TrainingTypeViewModel = hiltViewModel()
    val sportViewModel: SportViewModel = hiltViewModel()

    var showDialog by remember {
        mutableStateOf(false)
    }

    NavHost(navController, startDestination = AdminRoute.OVERVIEW.route) {
        composable(AdminRoute.OVERVIEW.route) {
            AdminPage(navController = navController)
        }

        composable(AdminRoute.TRAINING_TYPES.route) {
            DetailPage(
                title = "Trainingsarten",
                navController = navController,
                onClickAdd = {
                    showDialog = true
                }
            ) {
                var newTrainingType by remember {
                    mutableStateOf(TrainingType(name = ""))
                }

                TrainingTypeList(trainingTypeViewModel)

                if (showDialog) {

                    FullScreenDialog(
                        title = "Neue Trainingsart",
                        visible = showDialog,
                        onClose = { showDialog = false },
                        onSave = {
                            trainingTypeViewModel.insert(newTrainingType)
                            showDialog = false
                        }
                    ) {
                        AddTrainingType(newTrainingType)
                    }
                }
            }
        }

        composable(AdminRoute.SPORTS.route) {
            DetailPage(
                title = "Sportarten",
                navController = navController,
                onClickAdd = {
                    showDialog = true
                }
            ) {
                var newSport by remember {
                    mutableStateOf(Sport(name = ""))
                }

                SportList(viewModel = sportViewModel)
            }
        }
    }
}

@Composable
fun AdminPage(navController: NavController) {
    Column(
        Modifier
            .padding(10.dp)
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)) {
        ScreenTitle(title = "Administration")

        ListSection {
            ListItem(
                title = "Trainingsarten",
                description = "Verwalten der Kategorien (z. B. Ausdauer oder Kraft)",
                iconId = R.drawable.training_type,
            ) {
                navController.navigate(AdminRoute.TRAINING_TYPES.route)
            }

            ListDivider()

            ListItem(
                title = "Sportarten",
                description = "Verwalten der verfügbaren Sportarten",
                iconId = R.drawable.sports
            ) {
                navController.navigate(AdminRoute.SPORTS.route)
            }
        }
    }

}