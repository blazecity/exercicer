package ch.mobpro.exercicer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import ch.mobpro.exercicer.components.views.ReportingPage
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.ui.theme.ExercicerTheme
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        persistData()
        setContent {
            ExercicerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ReportingPage(trainingViewModel)
                }
            }
        }
    }

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
            trainingTimeSeconds = 20)

        val training2 = Training(7,
            LocalDate.of(2022, 2, 3), // 03/02/2022
            sport1.id,
            trainingTimeHour = 2)

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
    }
}
