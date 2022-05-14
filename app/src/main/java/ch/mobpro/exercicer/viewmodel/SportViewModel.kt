package ch.mobpro.exercicer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.repository.SportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SportViewModel @Inject constructor(private val repository: SportRepository): ViewModel() {

    private val _sportList = MutableStateFlow<List<Sport>>(emptyList())
    val sportList = _sportList.asStateFlow()

    private val _sportsMap = MutableStateFlow<Map<Sport, TrainingType>>(emptyMap())
    val sportMap = _sportsMap.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().distinctUntilChanged().collect { list ->
                if (!list.isNullOrEmpty()) _sportList.value = list
            }
        }

        viewModelScope.launch {
            repository.getAllJoined().distinctUntilChanged().collect { map ->
                if (!map.isNullOrEmpty()) _sportsMap.value = map
            }
        }
    }

    fun insert(sport: Sport) = viewModelScope.launch { repository.insert(sport) }
    fun delete(sport: Sport) = viewModelScope.launch { repository.delete(sport) }
    fun update(sport: Sport) = viewModelScope.launch { repository.update(sport) }
}