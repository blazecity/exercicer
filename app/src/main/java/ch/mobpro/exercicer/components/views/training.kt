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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ch.mobpro.exercicer.R
import ch.mobpro.exercicer.components.cards.*
import ch.mobpro.exercicer.components.date.DatePickerField
import ch.mobpro.exercicer.components.views.admin.TrainingTypeDialog
import ch.mobpro.exercicer.components.views.goals.FullScreenGoalDialog
import ch.mobpro.exercicer.components.views.goals.GoalCard
import ch.mobpro.exercicer.components.views.goals.GoalsList
import ch.mobpro.exercicer.data.entity.*
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import ch.mobpro.exercicer.data.util.getFormattedString
import ch.mobpro.exercicer.viewmodel.SportViewModel
import ch.mobpro.exercicer.viewmodel.TrainingTypeViewModel
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.selects.select
import java.time.LocalDate
import kotlin.math.roundToInt

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
                        newTraining = Training(date = LocalDate.now(), sportId = 0)
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
        Text("Noch keine Trainings aufgezeichnet.")
    }
}

@Composable
fun TrainingDialog(training: Training) {

    val context = LocalContext.current

    var comment by remember {
        mutableStateOf(training.comment)
    }

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

    var repeats by remember {
        mutableStateOf(training.repeats)
    }

    var sets by remember {
        mutableStateOf(training.sets)
    }

    var weight by remember {
        mutableStateOf(training.weight)
    }

    var intensity by remember {
        mutableStateOf(training.intensity)
    }

    val sportViewModel: SportViewModel = hiltViewModel()
    val allSports = sportViewModel.sportList.collectAsState().value

    var selectedSport by remember {
        mutableStateOf(
            if(allSports.isNotEmpty()) {
                allSports.find { it.id == training.sportId } ?: allSports.first()
            } else {
                Sport(name = "Wähle Sport") // Dummy Sport gewählt, da emptyList Exception
            }
        )
    }

    // Methode nötig, da allSports sonst zuerst immer leer ist und beim erneuten Durchlauf nicht angepasst wird.
    if (allSports.isNotEmpty()){
        selectedSport = allSports.find { it.id == training.sportId } ?: allSports.first()
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

        // If-Klausel nötig, da ansonsten beim ersten Mal die Sportart nicht richtig geladen wird (da zuerst allSports-Liste leer ist)
        if (allSports.isNotEmpty()) {
            Dropdown(
                title = "Sportart",
                list = allSports,
                selectedItem = selectedSport
            ) {
                selectedSport = it as Sport
                training.sportId = selectedSport.id!! //NPE bei Null
            }
        } else {
            Text(text = "Noch keine Sportart erfasst.", color = Color.Red)
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        if(selectedSport.hasTime) {
            SecondsTimePicker(hour, minute, second) { hours, minutes, secoonds ->
                hour = hours
                minute = minutes
                second = secoonds
                training.trainingTimeHour = hour
                training.trainingTimeMinutes = minute
                training.trainingTimeSeconds = second
            }

            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }

        if(selectedSport.hasDistance) {
            Row() {
                val distance2 = distance / currentDistanceUnit.multiplicator

                DistancePicker(distance2, training.distanceUnit) { dist, distUnit ->
                    distance = dist * distUnit.multiplicator
                    currentDistanceUnit = distUnit

                    training.trainingDistanceInMeters = distance
                    training.distanceUnit = currentDistanceUnit
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }

        if(selectedSport.hasNumberOfTimes) {

            Row (horizontalArrangement = Arrangement.SpaceBetween) {

                NumberInput(
                    modifier = Modifier.weight(1f),
                    label = "Wiederholungen",
                    initialValue = repeats,
                    onValueChange = {
                        repeats = it
                        training.repeats = repeats
                    }
                )

                NumberInput(
                    modifier = Modifier.weight(1f),
                    label = "Sets",
                    initialValue = sets,
                    onValueChange = {
                        sets = it
                        training.sets = sets
                    }
                )

                if (selectedSport.hasWeight) {
                    NumberInput(
                        modifier = Modifier.weight(1f),
                        label = "Gewicht (kg)",
                        initialValue = weight,
                        onValueChange = {
                            weight = it
                            training.weight = weight
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 10.dp))

        }

        if (selectedSport.hasIntensity){
            Text(text = "Intensität")

            Slider(
                value = intensity.toFloat(),
                valueRange = 0f..5f,
                steps = 4,
                onValueChange = {
                    intensity = it.roundToInt()
                    training.intensity = intensity
                })

            var text = when (intensity) {
                //if (intensity = 0f) {"keine Angabe"}
                //if (0f < intensity < 1f) {"wenig anstrengend"}
                0 -> "keine Angabe"
                1 -> "wenig anstrengend"
                2 -> "ein bisschen anstrengend"
                3 -> "mittelmässig"
                4 -> "eher schwer"
                5 -> "schwer"
                else -> {
                    "unklar"
                }
            }
            Text(
                text = text,
                modifier = Modifier.padding(start = 20.dp, top = 5.dp, bottom = 10.dp),
                color = Color.Gray,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            //.padding(top = 20.dp),
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

    var editableTraining by remember {
        mutableStateOf(trainingSportTrainingType.training)
    }

    var sport = trainingSportTrainingType.sport

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    BaseCard(onClick = { showEditDialog = true }) {
        CardContentRow {
            CardContentColumn(
                modifier = Modifier.width(68.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.running_55),
                    contentDescription = "Trainingsicon"
                )
            }
            CardContentColumn(
                modifier = Modifier.width(165.dp)
            ) {
                Text(
                    text = sport.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
                val dateAsString = editableTraining.date.getFormattedString() //Datum des Sports
                Text(dateAsString)
            }
            CardContentColumn(
                modifier = Modifier.width(100.dp)
            ) {
                // Nur zwei Daten sollen sichtbar sein
                var count = 0

                if (sport.hasTime && count < 2) {
                    val timeH = editableTraining.trainingTimeHour
                    val timeM = editableTraining.trainingTimeMinutes
                    val timeS = editableTraining.trainingTimeSeconds

                    if (timeH != 0) {
                        Text("$timeH h $timeM min")
                    } else if (timeH == 0 && timeS == 0) {
                        Text("$timeM min")
                    } else if (timeM != 0 && timeS != 0) {
                        Text("$timeM min $timeS s")
                    } else if (timeS != 0) {
                        Text("$timeS s")
                    }

                    count++
                }

                if (sport.hasDistance && count < 2) {
                    val distance = editableTraining.trainingDistanceInMeters
                    if (distance >= 1000) {
                        val distanceKm = distance / DistanceUnit.KILOMETERS.multiplicator
                        Text("$distanceKm km")
                    } else {
                        Text("$distance m")
                    }

                    count++
                }

                if (sport.hasNumberOfTimes && count < 2) {
                    val repeats = editableTraining.repeats
                    val sets = trainingSportTrainingType.training.sets
                    if (sets == 0) {
                        Text("$repeats Reps")
                    } else {
                        Text("$sets x $repeats Reps")
                    }

                    count++

                    if (sport.hasWeight && count < 2) {
                        val weight = editableTraining.weight
                        Text("$weight kg")

                        count++
                    }
                }

                if (sport.hasIntensity && count < 2) {
                    val intensity = editableTraining.intensity
                    if (intensity > 0) { // bei keiner Angabe wird nichts aufgeführt
                        Text("Intensität: $intensity")
                    }

                    count++
                }
            }
        }
    }

    if (showEditDialog) {
        FullScreenDialog(
            title = "Training bearbeiten",
            visible = showEditDialog,
            onClose = { showEditDialog = false },
            onSave = {
                trainingViewModel.update(editableTraining)
                showEditDialog = false
            }
        ) {
            TrainingDialog(training = editableTraining,)
        }
    }
}