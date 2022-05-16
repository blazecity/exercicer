package ch.mobpro.exercicer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ch.mobpro.exercicer.data.entity.mapping.SummingWrapper
import ch.mobpro.exercicer.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AchievementViewModel @Inject constructor(private val repository: TrainingRepository): ViewModel()  {

    fun getSportSumsByDate(sportId: Long, fromDate: LocalDate, toDate: LocalDate): LiveData<SummingWrapper> {
        return repository.getSportSumsByDate(sportId, fromDate, toDate).distinctUntilChanged().asLiveData()
    }

    fun getTrainingTypeSumsByDate(trainingTypeId: Long, fromDate: LocalDate, toDate: LocalDate): LiveData<SummingWrapper> {
        return repository.getTrainingTypeSumsByDate(trainingTypeId, fromDate, toDate).distinctUntilChanged().asLiveData()
    }
}