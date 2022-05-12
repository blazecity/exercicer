package ch.mobpro.exercicer.components.cards

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.test.core.app.ApplicationProvider
import ch.mobpro.exercicer.components.BaseCard
import ch.mobpro.exercicer.components.CardContentColumn
import ch.mobpro.exercicer.components.CardContentRow
import ch.mobpro.exercicer.components.CardTitleRow
import ch.mobpro.exercicer.components.cards.ui.theme.ExercicerTheme
import ch.mobpro.exercicer.data.db.AppDatabase
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.TrainingType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class training : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExercicerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    onLoad()
                }
            }
        }
    }
}

@Preview
@Composable
fun onLoad() {
    loadTraining()
}

@SuppressLint("CoroutineCreationDuringComposition") //Wegen GlobalScope nötig
@Composable
fun loadTraining(){
    var context = ApplicationProvider.getApplicationContext<Context>()
    val db: AppDatabase = AppDatabase.createDatabase(context)
    val trainingDao = db.trainingDao()
    val sportDao = db.sportDao()
    val trainingTypeDao = db.trainingTypeDao()

    // Testdata
    val trainingType = TrainingType(name = "Endurance")
    var trainingTypeId: Long = 0
    GlobalScope.launch {
        trainingTypeId = trainingTypeDao.insert(trainingType)
    }
    val sport = Sport(trainingTypeId = trainingTypeId, name = "Jogging")
    var sportId: Long = 0
    GlobalScope.launch {
        sportId = sportDao.insert(sport)
    }
    val training = Training(date = LocalDate.now(), sportId = sportId)
    var trainingID: Long = 0
    GlobalScope.launch {
        trainingID = trainingDao.insert(training)
    }

    var allTrainings: Map<Training, Sport>? = null
    GlobalScope.launch {
        allTrainings = trainingDao.getAll().first()
    }
    doTrainingCards(allTrainings = allTrainings)
}

@Composable
private fun doTrainingCards(allTrainings: Map<Training, Sport>?) {
    if (allTrainings != null) {
        for (a in allTrainings) {
            BaseCard {
                CardContentColumn {
                    CardContentRow {
                        //Image
                        Text("image")
                    }
                    CardContentRow {
                        Text(a.value.name) //Name des Sports
                        val dateInLong = a.key.date.toEpochDay() //Datum des Sports
                        Text("$a")
                    }
                    CardContentRow {
                        val trainingsID = a.value.trainingTypeId //ID der Kategorie
                        //val trainingType = trainingTypeDao.get(trainingsID)
                        //Text(trainingType.name)
                        val trainingTime = a.key.getFormattedTrainingTime() ?: "no Training time" // Trainingszeit falls vorhanden
                        Text(trainingTime)
                        val distance = a.key.getFormattedTrainingDistance() ?: a.key.intensity ?: "no data"
                        Text("$distance")
                    }
                }
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