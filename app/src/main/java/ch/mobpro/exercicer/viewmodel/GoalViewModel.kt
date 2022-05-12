package ch.mobpro.exercicer.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.mapping.GoalSportTrainingTypeMapping
import ch.mobpro.exercicer.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(private val repository: GoalRepository): ViewModel() {

    private val _goalList = MutableStateFlow<List<GoalSportTrainingTypeMapping>>(emptyList())
    val goalList = _goalList.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().distinctUntilChanged().collect { list ->
                if (!list.isNullOrEmpty()) _goalList.value = list
            }
        }
    }

    fun insert(goal: Goal) = viewModelScope.launch { repository.insert(goal) }

    fun delete(goal: Goal) = viewModelScope.launch { repository.delete(goal) }

    fun update(goal: Goal) = viewModelScope.launch { repository.update(goal) }
}