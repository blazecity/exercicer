package ch.mobpro.exercicer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import ch.mobpro.exercicer.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(private val repository: TrainingRepository) : ViewModel() {

    private val _reportingList = MutableStateFlow<List<TrainingSportTrainingTypeMapping>>(emptyList())
    val reportingList = _reportingList.asStateFlow()

    private val _trainingList = MutableStateFlow<List<TrainingSportTrainingTypeMapping>>(emptyList())
    val trainingList = _trainingList.asStateFlow()

    val fromDate = LocalDate.now()
    val toDate = LocalDate.now()

    init {
        viewModelScope.launch {
            repository.getAllByDate(fromDate, toDate).distinctUntilChanged().collect { list ->
                if (!list.isNullOrEmpty()) _reportingList.value = list
            }
        }

        viewModelScope.launch {
            repository.getAll().distinctUntilChanged().collect { list ->
                if (!list.isNullOrEmpty()) _trainingList.value = list
            }
        }
    }

    fun insert(training: Training) = viewModelScope.launch { repository.insert(training) }

    fun delete(training: Training) = viewModelScope.launch { repository.delete(training) }

    fun update(training: Training) = viewModelScope.launch { repository.update(training) }

}