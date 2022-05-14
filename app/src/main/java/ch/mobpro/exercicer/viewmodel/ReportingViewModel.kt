package ch.mobpro.exercicer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ch.mobpro.exercicer.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportingViewModel @Inject constructor(private val repository: TrainingRepository) : ViewModel() {
    private val dates = MutableStateFlow(Pair(LocalDate.of(LocalDate.now().year, 1,1), LocalDate.now()))

    private val _filteredTrainings = dates.flatMapLatest {
        repository.getAllByDate(it.first, it.second)
    }

    val filteredTrainings = _filteredTrainings.asLiveData()

    fun getFromDate(): LocalDate = dates.value.first
    fun setFromDate(fromDate: LocalDate) {
        dates.value = Pair(fromDate, dates.value.second)
    }
    fun getToDate(): LocalDate = dates.value.second
    fun setToDate(toDate: LocalDate) {
        dates.value = Pair(dates.value.first, toDate)
    }

}