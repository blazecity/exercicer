package ch.mobpro.exercicer.components.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import ch.mobpro.exercicer.components.*
import ch.mobpro.exercicer.components.cards.ui.theme.ExercicerTheme
import ch.mobpro.exercicer.components.views.Screens.Items.items
import ch.mobpro.exercicer.data.db.AppDatabase
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun trainingPage(reportingViewModel: TrainingViewModel) {
    //loadTraining()
    Column {
        TextTitle {
            Text("Trainings")
        }

        val list = reportingViewModel.trainingList.collectAsState().value // was müsste ich hier anders machen?
        if (list.isNotEmpty()){
            LazyColumn() {
                items(items = list) { list ->
                    doTrainingCards(trainingSportTrainingType = list)
                }
            }
        } else {
            Text("keine Trainings aufgezeichnet.")
        }




    }

}

@Composable
private fun doTrainingCards(trainingSportTrainingType: TrainingSportTrainingTypeMapping) {
    BaseCard {
        CardContentRow {
            CardContentColumn {
                //Image
                Text("image")
            }
            CardContentColumn {
                Text(trainingSportTrainingType.sport.name) //Name des Sports
                val dateInLong = trainingSportTrainingType.training.date.toEpochDay() //Datum des Sports
                Text("$trainingSportTrainingType")
            }
            CardContentColumn {
                val trainingsID = trainingSportTrainingType.sport.trainingTypeId //ID der Kategorie
                //val trainingType = trainingTypeDao.get(trainingsID)
                //Text(trainingType.name)
                //val trainingTime = a.key.getFormattedTrainingTime() ?: "no Training time" // Trainingszeit falls vorhanden
                //Text(trainingTime)
                //val distance = a.key.getFormattedTrainingDistance() ?: a.key.intensity ?: "no data"
                //Text("$distance")
                // Übergangslösung
                val time = trainingSportTrainingType.training.trainingTimeHour
                Text("Hour: $time")
                val distanceUnit = trainingSportTrainingType.training.distanceUnit
                Text("Distanz: $distanceUnit")
            }
        }
    }
}

@Preview
@Composable
fun test(){
    BaseCard {
        CardContentColumn {
            CardTitleRow {
                Text("Hello World")
                Text("22")
            }
            CardContentRow {
                Text("Rowrow")
                Text("Rowrow2")
            }
            CardContentRow {
                Text("Rowrow3")
                Text("Rowrow4")
            }
            Text("hihi")
            Text("hihi")
        }

    }
}