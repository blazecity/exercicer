package ch.mobpro.exercicer.components.views

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import ch.mobpro.exercicer.components.*
import ch.mobpro.exercicer.data.entity.Training
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import ch.mobpro.exercicer.components.cards.*
import ch.mobpro.exercicer.components.date.DatePickerField
import ch.mobpro.exercicer.components.views.admin.TrainingTypeDialog
import ch.mobpro.exercicer.components.views.goals.FullScreenGoalDialog
import ch.mobpro.exercicer.components.views.goals.GoalCard
import ch.mobpro.exercicer.components.views.goals.GoalsList
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import ch.mobpro.exercicer.data.util.getFormattedString
import ch.mobpro.exercicer.viewmodel.SportViewModel
import ch.mobpro.exercicer.viewmodel.TrainingTypeViewModel
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate

@Composable
fun TrainingPage() {
    val trainingViewModel: TrainingViewModel = hiltViewModel()

    var showDialog by remember {
        mutableStateOf(false) // false setzen, wenn anders implementiert
    }

    var newTraining by remember {
        mutableStateOf(Training(date = LocalDate.now(), sportId = 0))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Default.Add, "add button")
            }
        }
    ) { paddingValues ->
        Page(modifier = Modifier.padding(paddingValues), title = "Training") {
            TrainingList(trainingViewModel)

            if (showDialog) {
                FullScreenDialog(
                    title = "Neues Training",
                    visible = showDialog,
                    onClose = { showDialog = false },
                    onSave = {
                        trainingViewModel.insert(newTraining)
                        showDialog = false
                    }
                ) {
                    TrainingDialog(newTraining)
                }
            }
        }
    }
}

@Composable
fun TrainingEdit(trainingViewModel: TrainingViewModel){

}

@Composable
fun TrainingList(trainingViewModel: TrainingViewModel){
    val trainingList = trainingViewModel.trainingList.collectAsState().value
    TrainingEdit(trainingViewModel)
    if (trainingList.isNotEmpty()){
        LazyColumn() {
            items(trainingList, key = {item -> item.training.id!!}, itemContent = { item ->
                ItemDeleteAction(
                    item = item.training,
                    dismissAction = { trainingToDismiss ->
                        trainingViewModel.delete(trainingToDismiss as Training)
                    }
                ) {
                    DoTrainingCards(item, trainingViewModel)
                }
            })

        }
    } else {
        Text("noch keine Trainings aufgezeichnet.")
    }
}

@Composable
fun TrainingDialog(training: Training) {
    var comment by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    var hour by remember {
        mutableStateOf(training.trainingTimeHour)
    }
    var minute by remember {
        mutableStateOf(training.trainingTimeMinutes)
    }
    var second by remember {
        mutableStateOf(training.trainingTimeSeconds)
    }

    var distance by remember {
        mutableStateOf(training.trainingDistanceInMeters)
    }

    var currentDistanceUnit by remember {
        mutableStateOf(training.distanceUnit)
    }

    val sportViewModel: SportViewModel = hiltViewModel()
    var allSports = sportViewModel.sportList.collectAsState().value

    var selectedSport by remember {
        mutableStateOf(
            if(allSports.isNotEmpty()) {
                allSports.find { it.id == training.sportId } ?: allSports.first()
            } else {
                Sport(1, "Wähle Sport", 1!!) // Dummy Sport gewählt, da emptyList Exception
            })
    }

    val date by remember {
        mutableStateOf(LocalDate.now())
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                DatePickerField(date, "Datum") { date ->
                    training.date = date
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        val trainings = sportViewModel.sportList.collectAsState().value
        //val selectedSport = sportViewModel.getSportById(training.sportId)
        Dropdown(
            title = "Sportart",
            list = allSports,
            selectedItem = selectedSport
        ){
            training.sportId = (it as Sport).id!! //NPE bei Null
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        //Row() {
        //    OutlinedTextField(
        //        modifier = Modifier
        //            .padding(top = 20.dp),
        //        label = { Text("Stunden") },
        //        value = if(hour != 0){ hour.toString()} else {""},
        //        onValueChange = {
        //            try {
        //                hour = it.toInt()
        //            } catch (e: NumberFormatException){
        //                //Text("Gib bitte eine Nummer ein!")
        //                Toast.makeText(context, "Gib bitte eine Nummer ein!", Toast.LENGTH_SHORT)
        //                Log.e("NotNumber", "Error: Not a Number in hour-Field")
        //            } finally {
        //                training.trainingTimeHour = hour
        //            }
        //        }
        //    )
        //    OutlinedTextField(
        //        modifier = Modifier
        //            .padding(top = 20.dp),
        //        label = { Text("Minuten") },
        //        value = if(minute != 0){ minute.toString()} else {""},
        //        onValueChange = {
        //            try {
        //                minute = it.toInt()
        //            } catch (e: NumberFormatException){
        //                //Text("Gib bitte eine Nummer ein!")
        //                Toast.makeText(context, "Gib bitte eine Nummer ein!", Toast.LENGTH_SHORT)
        //                Log.e("NotNumber", "Error: Not a Number in minute-Field")
        //            } finally {
        //                training.trainingTimeMinutes = minute
        //            }
        //        }
        //    )
        //
        //}
        //Spacer(modifier = Modifier.padding(vertical = 10.dp))

        SecondsTimePicker(hour, minute, second) { hours, minutes, secoonds ->
            hour = hours
            minute = minutes
            second = secoonds
            training.trainingTimeHour = hour
            training.trainingTimeMinutes = minute
            training.trainingTimeSeconds = second
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        Row() {
            //OutlinedTextField(
            //    modifier = Modifier
            //        .padding(top = 20.dp),
            //    label = { Text("Distanz") },
            //    value = if(distance != 0){ distance.toString()} else {""},
            //    onValueChange = {
            //        try {
            //            distance = it.toInt()
            //        } catch (e: NumberFormatException){
            //            Toast.makeText(context, "Gib bitte eine Nummer ein!", Toast.LENGTH_SHORT)
            //            Log.e("NotNumber", "Error: Not a Number in distance-Field")
            //        } finally {
            //            training.trainingDistanceInMeters = distance
            //        }
            //    }
            //)
            //Dropdown(
            //   title = "Einheit",
            //   list = allSports,
            //   //selectedItem = allSports.first() //selectedSport muss noch hinzugefügt werden, falls Sport schon ausgewählt
            //{
            //   training.sportId = (it as Sport).id!! //NPE bei Null
            //

            val distance2 = distance / currentDistanceUnit.multiplicator

            DistancePicker(distance2, training.distanceUnit) { dist, distUnit ->
                distance = dist * distUnit.multiplicator
                currentDistanceUnit = distUnit

                training.trainingDistanceInMeters = distance
                training.distanceUnit = currentDistanceUnit
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            label = { Text("Kommentar") },
            value = comment,
            onValueChange = {
                comment = it
                training.comment = it
            }
        )
    }
}

@Composable
private fun DoTrainingCards(trainingSportTrainingType: TrainingSportTrainingTypeMapping, trainingViewModel: TrainingViewModel) {
    var showEditDialog by remember {
        mutableStateOf(false)
    }

    BaseCard(onClick = { showEditDialog = true }) {
        CardContentRow(
            //Modifier.clickable(onClick = TrainingDialog(training = trainingSportTrainingType.training)
        ) {
            CardContentColumn {
                //Image
                Text("image")
            }
            CardContentColumn {
                Text(trainingSportTrainingType.sport.name) //Name des Sports
                val dateAsString = trainingSportTrainingType.training.date.getFormattedString() //Datum des Sports
                Text(dateAsString)
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
                val timeH = trainingSportTrainingType.training.trainingTimeHour
                val timeM = trainingSportTrainingType.training.trainingTimeMinutes
                val timeS = trainingSportTrainingType.training.trainingTimeSeconds
                if (timeH != null || timeM != null) { // keine Sekundenangabe, da zu detailliert
                    if (timeH != null && timeM != null) {
                        Text("$timeH h $timeM min" )
                    } else if (timeH == null){
                        Text("$timeM min" )
                    } else if (timeM == null) {
                        Text("$timeH h")
                    }
                } else (Text("keine Zeitangabe"))
                val distance = trainingSportTrainingType.training.trainingDistanceInMeters
                if (distance != null) {
                    if (distance > 1000) {
                        val distanceKm: Float = distance.toFloat() / 1000
                        Text("Distanz: $distanceKm km")
                    } else {
                        Text("Distanz: $distance m")
                    }
                } else (Text("keine Distanzangabe"))

            }
        }
    }

    val context = LocalContext.current

    var editableTraining by remember {
        mutableStateOf(Training(date = LocalDate.now(), sportId = 0))
    }

    if (showEditDialog) {
        FullScreenDialog(
            title = "Training bearbeiten",
            visible = showEditDialog,
            onClose = { showEditDialog = false },
            onSave = {
                trainingViewModel.update(editableTraining)
                showEditDialog = false
                editableTraining = Training(date = LocalDate.now(), sportId = 0)
            }
        ) {
            TrainingDialog(training = editableTraining,)
        }
    }

    //if (showEditDialog) {
    //    FullScreenDialog(
    //        title = "Training bearbeiten",
    //        goal = training,
    //        sportViewModel = trainingViewModel,
    //        visibilityChange = { visibility -> showEditDialog = visibility }
    //    ) { validationSuccessful, validationMessage ->
    //        if (!validationSuccessful) {
    //            Toast.makeText(context, validationMessage, Toast.LENGTH_LONG).show()
    //        } else {
    //            trainingViewModel.update(training)
    //            showDialog = false
    //        }
    //    }
    //}
}