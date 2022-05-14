package ch.mobpro.exercicer.components.views.admin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.mobpro.exercicer.components.FullScreenDialog
import ch.mobpro.exercicer.components.ListDeleteAction
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.viewmodel.TrainingTypeViewModel

@Composable
fun TrainingTypeList(viewModel: TrainingTypeViewModel) {
    val items = viewModel.trainingTypeList.collectAsState().value

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var editableTrainingType by remember {
        mutableStateOf(TrainingType(name = ""))
    }

    ListDeleteAction(list = items, dismissAction = { viewModel.delete(it as TrainingType) }) { item ->
        editableTrainingType = (item as TrainingType).copy()
        showEditDialog = true
    }

    if (showEditDialog) {
        FullScreenDialog(
            title = "Trainingsart bearbeiten",
            visible = showEditDialog,
            onClose = { showEditDialog = false },
            onSave = {
                viewModel.update(editableTrainingType)
                showEditDialog = false
                editableTrainingType = TrainingType(name = "")
            }
        ) {
            TrainingTypeDialog(trainingType = editableTrainingType)
        }
    }
}

@Composable
fun TrainingTypeDialog(trainingType: TrainingType) {
    var text by remember {
        mutableStateOf(trainingType.name)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        label = { Text("Name") },
        value = text,
        onValueChange = {
            trainingType.name = it
            text = it
        }
    )
}