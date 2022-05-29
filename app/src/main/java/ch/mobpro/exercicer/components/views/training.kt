package ch.mobpro.exercicer.components.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.mobpro.exercicer.R
import ch.mobpro.exercicer.components.*
import ch.mobpro.exercicer.components.cards.BaseCard
import ch.mobpro.exercicer.components.cards.CardContentColumn
import ch.mobpro.exercicer.components.cards.CardContentRow
import ch.mobpro.exercicer.components.cards.SmallBadge
import ch.mobpro.exercicer.components.date.DatePickerField
import ch.mobpro.exercicer.data.entity.DistanceUnit
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import ch.mobpro.exercicer.data.util.*
import ch.mobpro.exercicer.viewmodel.SportViewModel
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun TrainingPage() {
    val trainingViewModel: TrainingViewModel = hiltViewModel()
    val sportViewModel: SportViewModel = hiltViewModel()
    val sportList = sportViewModel.sportList.collectAsState().value
    val context = LocalContext.current

    var showDialog by remember {
        mutableStateOf(false) // false setzen, wenn anders implementiert
    }

    var newTraining by remember {
        mutableStateOf(Training(date = LocalDate.now(), sportId = 0))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (sportList.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Es sind keine Sportarten erfasst",
                        Toast.LENGTH_LONG
                    ).show()
                } else showDialog = true
            }) {
                Icon(Icons.Default.Add, "add button")
            }
        }
    ) { paddingValues ->
        Page(modifier = Modifier.padding(paddingValues), title = "Training") {
            TrainingList(trainingViewModel, sportList)

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
                    TrainingDialog(newTraining, sportList)
                }
            }
        }
    }
}

@Composable
fun TrainingList(trainingViewModel: TrainingViewModel, sportList: List<Sport>){
    val trainingList = trainingViewModel.trainingList.collectAsState().value

    if (trainingList.isNotEmpty()){
        LazyColumn {
            items(trainingList, key = {item -> item.training.id!!}, itemContent = { item ->
                ItemDeleteAction(
                    item = item.training,
                    dismissAction = { trainingToDismiss ->
                        trainingViewModel.delete(trainingToDismiss as Training)
                    }
                ) {
                    DoTrainingCards(item, trainingViewModel, sportList)
                }
            })

        }
    } else {
        Text("Noch keine Trainings aufgezeichnet.")
    }
}

@Composable
fun TrainingDialog(training: Training, allSports: List<Sport>) {

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

    var selectedSport by remember {
        val sport = allSports.find { it.id == training.sportId } ?: allSports.firstOrNull() ?: Sport(name = "")
        training.sportId = sport.id!!
        mutableStateOf(sport)
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
        //if (allSports.isNotEmpty()) {
            Dropdown(
                title = "Sportart",
                list = allSports,
                selectedItem = selectedSport
            ) {
                selectedSport = it as Sport
                training.sportId = selectedSport.id!! //NPE bei Null
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
private fun DoTrainingCards(
    trainingSportTrainingType: TrainingSportTrainingTypeMapping,
    trainingViewModel: TrainingViewModel? = null,
    sportList: List<Sport>
) {

    val editableTraining by remember {
        mutableStateOf(trainingSportTrainingType.training)
    }

    val sport = trainingSportTrainingType.sport

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
                SmallBadge(content = sport.name)
                val dateAsString = editableTraining.date.getFormattedString() //Datum des Sports
                Text(dateAsString)
            }
            CardContentColumn(
                modifier = Modifier.width(100.dp)
            ) {
                if (sport.hasTime) {
                    val timeH = editableTraining.trainingTimeHour
                    val timeM = editableTraining.trainingTimeMinutes
                    val timeS = editableTraining.trainingTimeSeconds

                    Text(getFormattedTime(timeH, timeM, timeS))
                }

                if (sport.hasDistance) {
                    val distance = editableTraining.trainingDistanceInMeters
                    if (distance >= 1000) {
                        Text(getFormattedDistance(distance)!!)
                    } else {
                        Text(getFormattedDistance(distance, DistanceUnit.METERS)!!)
                    }
                }

                if (sport.hasNumberOfTimes) {
                    val repeats = editableTraining.repeats
                    val sets = trainingSportTrainingType.training.sets
                    if (sets == 0) {
                        Text("$repeats Reps")
                    } else {
                        Text("$sets x $repeats Reps")
                    }

                    if (sport.hasWeight) {
                        val weight = editableTraining.weight
                        Text(getFormattedWeight(weight))
                    }
                }

                if (sport.hasIntensity) {
                    val intensity = editableTraining.intensity
                    if (intensity > 0) { // bei keiner Angabe wird nichts aufgeführt
                        Text("Intensität: $intensity")
                    }
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
                trainingViewModel?.update(editableTraining)
                showEditDialog = false
            }
        ) {
            TrainingDialog(training = editableTraining, sportList)
        }
    }
}