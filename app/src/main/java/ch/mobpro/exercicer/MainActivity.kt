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
        persistData()
        setContent {
            ExercicerTheme {
                NavigationController()
            }
        }
    }

    // sample data
    private fun persistData() {
        val trainingType1 = TrainingType(id = 1,name = "Ausdauer")
        val trainingType2 = TrainingType(id = 2,name =  "Kraft")
        val trainingType3 = TrainingType(id = 3,name =  "Training")
        trainingTypeViewModel.insert(trainingType1)
        trainingTypeViewModel.insert(trainingType2)
        trainingTypeViewModel.insert(trainingType3)

        val sport1 = Sport(id = 4, name = "Joggen", trainingTypeId = trainingType1.id!!)
        val sport2 = Sport(id = 5,name =  "Beinpresse", trainingTypeId = trainingType2.id!!)
        val sport3 = Sport(id = 6,name =  "Liegestütze", trainingTypeId = trainingType2.id)
        val sport4 = Sport(id = 7,name =  "Fussballtraining", trainingTypeId = trainingType3.id)

        sport1.hasDistance = true
        sport1.hasTime = true
        sport2.hasNumberOfTimes = true
        sport2.hasWeight = true
        sport2.hasIntensity = true
        sport3.hasNumberOfTimes = true
        sport3.hasIntensity = true
        sport4.hasTime = true
        sport4.hasIntensity = true

        sportViewModel.insert(sport1)
        sportViewModel.insert(sport2)
        sportViewModel.insert(sport3)
        sportViewModel.insert(sport4)

        val training1 = Training(
            id = 8,
            date = LocalDate.of(2021, 4, 6), // 06/04/2021
            sportId = sport1.id!!,
            trainingTimeMinutes = 20,
            trainingTimeSeconds = 20,
            trainingDistanceInMeters = 1000f
        )

        val training2 = Training(
            id = 9,
            date = LocalDate.of(2022, 2, 3), // 03/02/2022
            sportId = sport2.id!!,
            trainingTimeHour = 2,
            sets = 3,
            repeats = 12
        )

        val training3 = Training(
            id = 10,
            date = LocalDate.of(2022, 2, 4), // 04/02/2022
            sportId = sport3.id!!,
            trainingTimeMinutes = 34,
            sets = 3,
            repeats = 20,
            weight = 120f
        )

        val training4 = Training(
            id = 11,
            date = LocalDate.of(2022, 3, 17), // 17/03/2022
            sportId = sport1.id!!,
            trainingTimeMinutes = 43,
            trainingTimeSeconds = 12,
            trainingDistanceInMeters = 8400f
        )

        val training5 = Training(
            id = 12,
            date = LocalDate.of(2022, 5, 6), // 17/03/2022
            sportId = sport4.id!!,
            trainingTimeHour = 3,
            trainingTimeSeconds = 4,
            intensity = 3
        )

        trainingViewModel.insert(training1)
        trainingViewModel.insert(training2)
        trainingViewModel.insert(training3)
        trainingViewModel.insert(training4)
        trainingViewModel.insert(training5)

        val goal1 = Goal(
            start = LocalDate.of(2021, 4, 6),
            end = LocalDate.now(),
            sportId = sport1.id,
            distanceGoalInMetres = 1000f,
            distanceUnit = DistanceUnit.KILOMETERS,
            trainingTimeGoalHours = 3,
            numberOfTimesGoal = 2,
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
