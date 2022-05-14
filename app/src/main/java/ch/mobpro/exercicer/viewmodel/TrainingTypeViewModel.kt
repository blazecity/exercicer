package ch.mobpro.exercicer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.repository.TrainingTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingTypeViewModel @Inject constructor(private val repository: TrainingTypeRepository): ViewModel() {

    private val _trainingTypeList = MutableStateFlow<List<TrainingType>>(emptyList())
    val trainingTypeList = _trainingTypeList.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().distinctUntilChanged().collect { list ->
                if (!list.isNullOrEmpty()) _trainingTypeList.value = list
            }
        }
    }

    fun insert(trainingType: TrainingType) = viewModelScope.launch { repository.insert(trainingType) }
    fun delete(trainingType: TrainingType) = viewModelScope.launch { repository.delete(trainingType) }
    fun update(trainingType: TrainingType) = viewModelScope.launch { repository.update(trainingType) }
}