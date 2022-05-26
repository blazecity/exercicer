package ch.mobpro.exercicer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.mobpro.exercicer.components.views.NavigationController
import ch.mobpro.exercicer.data.entity.*
import ch.mobpro.exercicer.ui.theme.ExercicerTheme
import ch.mobpro.exercicer.viewmodel.GoalViewModel
import ch.mobpro.exercicer.viewmodel.SportViewModel
import ch.mobpro.exercicer.viewmodel.TrainingTypeViewModel
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val trainingViewModel: TrainingViewModel by viewModels()
    private val sportViewModel: SportViewModel by viewModels()
    private val trainingTypeViewModel: TrainingTypeViewModel by viewModels()
    private val goalViewModel: GoalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // persistData()
        setContent {
            ExercicerTheme {
                NavigationController()
            }
        }
    }

    // sample data
    private fun persistData() {
        val trainingType1 = TrainingType(1, "Training Type 1")
        val trainingType2 = TrainingType(2, "Training Type 2")
        trainingTypeViewModel.insert(trainingType1)
        trainingTypeViewModel.insert(trainingType2)

        val sport1 = Sport(3, "Sport 1", trainingType1.id!!)
        val sport2 = Sport(4, "Sport 2", trainingType2.id!!)
        val sport3 = Sport(5, "Sport 3", trainingType2.id)
        sportViewModel.insert(sport1)
        sportViewModel.insert(sport2)
        sportViewModel.insert(sport3)

        val training1 = Training(6,
            LocalDate.of(2021, 4, 6), // 06/04/2021
            sport1.id!!,
            trainingTimeMinutes = 20,
            trainingTimeSeconds = 20,
            trainingDistanceInMeters = 10f,
            sets = 2,
            repeats = 12,
            weight = 20f
        )

        val training2 = Training(7,
            LocalDate.of(2022, 2, 3), // 03/02/2022
            sport1.id,
            trainingTimeHour = 2,
            sets = 3,
            repeats = 12,
            weight = 40f
        )

        val training3 = Training(8,
            LocalDate.of(2022, 2, 4), // 04/02/2022
            sport2.id!!,
            trainingTimeMinutes = 34)

        val training4 = Training(9,
            LocalDate.of(2022, 3, 17), // 17/03/2022
            sport3.id!!,
            trainingTimeHour = 3,
            trainingTimeSeconds = 4)

        trainingViewModel.insert(training1)
        trainingViewModel.insert(training2)
        trainingViewModel.insert(training3)
        trainingViewModel.insert(training4)

        val goal1 = Goal(
            start = LocalDate.of(2021, 4, 6),
            end = LocalDate.now(),
            sportId = sport1.id,
            distanceGoalInMetres = 1000f,
            distanceUnit = DistanceUnit.KILOMETERS,
            trainingTimeGoalHours = 3,
            numberOfTimesGoal = 2,
            weightGoal = 41.5f
        )

        val goal2 = Goal(
            start = LocalDate.of(2022, 1, 1),
            end = LocalDate.of(2022, 5, 1),
            trainingTypeId = trainingType2.id,
            trainingTimeGoalHours = 2
        )

        val goal3 = Goal(
            start = LocalDate.of(2021, 4, 5),
            end = LocalDate.of(2021, 4, 7),
            trainingTypeId = trainingType1.id,
            trainingTimeGoalHours = 5
        )

        goalViewModel.insert(goal1)
        goalViewModel.insert(goal2)
        goalViewModel.insert(goal3)

    }
}
