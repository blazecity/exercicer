package ch.mobpro.exercicer.components.views

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
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
import ch.mobpro.exercicer.components.cards.*
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import ch.mobpro.exercicer.viewmodel.SportViewModel
import ch.mobpro.exercicer.viewmodel.TrainingViewModel
import java.time.LocalDate

@Composable
fun TrainingPage() {
    val trainingViewModel: TrainingViewModel = hiltViewModel()

    Column(
        Modifier
            .padding(10.dp)
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
    ) {
        ScreenTitle(title = "Training")

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

        AddTrainingDialog()
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter") // weil im Scaffold Teil die Klammern nicht gemraucht werden
@Preview(showBackground = true)
@Composable
private fun AddTrainingDialog(){

    var showDialog by remember {
        mutableStateOf(false) // false setzen, wenn anders implementiert
    }
    val trainingViewModel: TrainingViewModel = hiltViewModel()

    var newTraining by remember {
        mutableStateOf(Training(date = LocalDate.MAX, sportId = 0))
    }

    // geht noch nicht
    FloatingActionButton(onClick = { showDialog = true }) {
        Icon(Icons.Default.Add, "add button")
    }


    FullScreenDialog(
        title = "Neues Training",
        visible = showDialog,
        onClose = { showDialog = false },
        onSave = {
            trainingViewModel.insert(newTraining)
            showDialog = false
        }
    ) {
        AddTraining(newTraining)
    }

}

@Composable
fun AddTraining(newTraining: Training) {
    var text by remember {
        mutableStateOf("")
    }

    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (dropdownExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val sportViewModel: SportViewModel = hiltViewModel()
    var allSports = sportViewModel.sportList.collectAsState().value

    var selectedText by remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        value = selectedText,
        onValueChange = { selectedText = it },
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                // This value is used to assign to the DropDown the same width
                mTextFieldSize = coordinates.size.toSize()
            },
        readOnly = true,
        label = {Text("Sportart")},
        trailingIcon = {
            Icon(icon,"contentDescription",
                Modifier.clickable { dropdownExpanded = !dropdownExpanded })
        }
    )
    DropdownMenu(
        expanded = dropdownExpanded,
        onDismissRequest = { dropdownExpanded = false },
        modifier = Modifier
            .width(with(LocalDensity.current){mTextFieldSize.width.toDp()}),
    ) {
        allSports.forEach { label ->
            DropdownMenuItem(onClick = {
                selectedText = label.name
                newTraining.sportId = label.id ?: 0 // Fehler: falls Label 0 => SportID 0
                dropdownExpanded = false
            }) {
                Text(text = label.name)
            }
        }
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        label = { Text("Name") },
        value = text,
        onValueChange = {
            text = it
            newTraining.comment = it
        }
    )
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