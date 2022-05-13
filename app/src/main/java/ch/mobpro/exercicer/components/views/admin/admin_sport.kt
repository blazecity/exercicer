package ch.mobpro.exercicer.components.views.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import ch.mobpro.exercicer.components.ListDeleteAction
import ch.mobpro.exercicer.viewmodel.SportViewModel

@Composable
fun SportList(viewModel: SportViewModel) {
    val items = viewModel.sportList.collectAsState().value
    ListDeleteAction(items = items, dismissAction = { viewModel.delete(it) })
}