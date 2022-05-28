package ch.mobpro.exercicer.components.views.admin

import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    val trainingTypeList = trainingTypeViewModel.trainingTypeList.collectAsState().value
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

                TrainingTypeList(trainingTypeViewModel, trainingTypeList)

                if (showDialog) {

                    FullScreenDialog(
                        title = "Neue Trainingsart",
                        visible = showDialog,
                        onClose = { showDialog = false },
                        onSave = {
                            trainingTypeViewModel.insert(newTrainingType)
                            showDialog = false
                            newTrainingType = TrainingType(name = "")
                        }
                    ) {
                        TrainingTypeDialog(newTrainingType)
                    }
                }
            }
        }

        composable(AdminRoute.SPORTS.route) {
            val context = LocalContext.current

            DetailPage(
                title = "Sportarten",
                navController = navController,
                onClickAdd = {
                    if (trainingTypeList.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Es sind noch keine Trainingsarten erfasst.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else showDialog = true
                }
            ) {
                var newSport by remember {
                    mutableStateOf(Sport(name = ""))
                }

                SportList(sportViewModel, trainingTypeViewModel)

                if (showDialog) {

                    FullScreenDialog(
                        title = "Neue Sportart",
                        visible = showDialog,
                        onClose = { showDialog = false },
                        onSave = {
                            sportViewModel.insert(newSport)
                            showDialog = false
                            newSport = Sport(name = "")
                        }
                    ) {
                        SportDialog(newSport, trainingTypeList.first(), trainingTypeList)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPage(navController: NavController) {
    Page(title = "Administration") {
        Column(
            Modifier
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical)) {

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

}