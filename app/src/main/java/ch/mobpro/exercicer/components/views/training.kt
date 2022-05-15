package ch.mobpro.exercicer.components.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ch.mobpro.exercicer.components.cards.*
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import ch.mobpro.exercicer.viewmodel.TrainingViewModel

@Composable
fun TrainingPage() {
    val trainingViewModel: TrainingViewModel = hiltViewModel()
    //loadTraining()
    Column {
        TextTitle {
            Text("Trainings")
        }

        val list = trainingViewModel.trainingList.collectAsState().value // was müsste ich hier anders machen?
        if (list.isNotEmpty()){
            LazyColumn() {
                items(items = list) { list ->
                    DoTrainingCards(trainingSportTrainingType = list)
                }
            }
        } else {
            Text("keine Trainings aufgezeichnet.")
        }
    }

}

@Composable
private fun DoTrainingCards(trainingSportTrainingType: TrainingSportTrainingTypeMapping) {
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
fun Test(){
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