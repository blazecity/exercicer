package ch.mobpro.exercicer.components.views.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.mobpro.exercicer.components.Dropdown
import ch.mobpro.exercicer.components.FullScreenDialog
import ch.mobpro.exercicer.components.LabeledSwitch
import ch.mobpro.exercicer.components.ListDeleteAction
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.viewmodel.SportViewModel
import ch.mobpro.exercicer.viewmodel.TrainingTypeViewModel

@Composable
fun SportList(viewModel: SportViewModel, trainingTypeViewModel: TrainingTypeViewModel) {
    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var editableSport by remember {
        mutableStateOf(Sport(name = ""))
    }

    val sportMap = viewModel.sportMap.collectAsState().value
    val list = sportMap.keys.toList()
    ListDeleteAction(list = list.toMutableList(), dismissAction = { viewModel.delete(it as Sport) }) { item ->
        editableSport = (item as Sport).copy()
        showEditDialog = true
    }

    if (showEditDialog) {
        FullScreenDialog(
            title = "Sportart bearbeiten",
            visible = showEditDialog,
            onClose = { showEditDialog = false },
            onSave = {
                viewModel.update(editableSport)
                showEditDialog = false
                editableSport = Sport(name = "")
            }
        ) {
            val trainingTypes = trainingTypeViewModel.trainingTypeList.collectAsState().value
            val trainingType = sportMap[editableSport]
            SportDialog(editableSport, trainingType ?: trainingTypes.first(), trainingTypes)
        }
    }
}


@Composable
fun SportDialog(sport: Sport, trainingType: TrainingType, list: List<TrainingType>) {
    var text by remember {
        mutableStateOf(sport.name)
    }

    var hasTime by remember {
        mutableStateOf(sport.hasTime)
    }

    var hasDistance by remember {
        mutableStateOf(sport.hasDistance)
    }

    var hasNumberOfTimes by remember {
        mutableStateOf(sport.hasNumberOfTimes)
    }

    var hasWeight by remember {
        mutableStateOf(sport.hasWeight)
    }

    sport.trainingTypeId = trainingType.id

    Column {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            label = { Text("Name") },
            value = text,
            onValueChange = {
                sport.name = it
                text = it
            }
        )

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        Dropdown(
            title = "Trainingsart",
            list = list,
            selectedItem =  trainingType
        ) {
            sport.trainingTypeId = (it as TrainingType).id
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        LabeledSwitch(
            initialState = hasTime,
            label = "Kann Zeit haben",
            onCheckedChange = {
                hasTime = it
                sport.hasTime = hasTime
            }
        )

        LabeledSwitch(
            initialState = hasDistance,
            label = "Kann Distanz haben",
            onCheckedChange = {
                hasDistance = it
                sport.hasDistance = hasDistance
            }
        )

        LabeledSwitch(
            initialState = hasNumberOfTimes,
            label = "Kann Anzahl Wiederholungen haben",
            onCheckedChange = {
                hasNumberOfTimes = it
                sport.hasNumberOfTimes = hasNumberOfTimes
            }
        )

        LabeledSwitch(
            initialState = hasWeight,
            label = "Kann Trainingsgewicht haben",
            onCheckedChange = {
                hasWeight = it
                sport.hasWeight = hasWeight
            }
        )
    }

}