package ch.mobpro.exercicer.components.views.admin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.mobpro.exercicer.components.ListDeleteAction
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.viewmodel.TrainingTypeViewModel

@Composable
fun TrainingTypeList(viewModel: TrainingTypeViewModel) {
    val items = viewModel.trainingTypeList.collectAsState().value
    ListDeleteAction(items = items, dismissAction = { viewModel.delete(it) })
}

@Composable
fun AddTrainingType(trainingType: TrainingType) {
    var text by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        label = { Text("Name") },
        value = text,
        onValueChange = {
            text = it
            trainingType.name = it
        }
    )
}