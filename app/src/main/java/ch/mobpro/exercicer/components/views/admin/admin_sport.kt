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
    ListDeleteAction(list = sportMap.keys.toList(), dismissAction = { viewModel.delete(it as Sport) }) { item ->
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
    }

}