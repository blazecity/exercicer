package ch.mobpro.exercicer.data.repository

import ch.mobpro.exercicer.data.dao.TrainingDao
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import javax.inject.Inject

class TrainingRepository @Inject constructor(private val dao: TrainingDao) {
    suspend fun insert(training: Training): Long = dao.insert(training)
    suspend fun delete(training: Training) = dao.delete(training)
    suspend fun update(training: Training) = dao.update(training)
    fun getAll(): Flow<List<TrainingSportTrainingTypeMapping>> =
        dao.getAll().flowOn(Dispatchers.Default).conflate()
    fun getAllByDate(fromDate: LocalDate, toDate: LocalDate): Flow<List<TrainingSportTrainingTypeMapping>> =
        dao.getAllByDate(fromDate, toDate).flowOn(Dispatchers.Default).conflate()
}