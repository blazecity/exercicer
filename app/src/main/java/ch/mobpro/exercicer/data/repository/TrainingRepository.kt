package ch.mobpro.exercicer.data.repository

import ch.mobpro.exercicer.data.dao.TrainingDao
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class TrainingRepository @Inject constructor(private val dao: TrainingDao) {
    suspend fun insert(training: Training): Long = dao.insert(training)

    suspend fun delete(training: Training) = dao.delete(training)

    suspend fun update(training: Training) = dao.update(training)

    fun getAll(): Flow<List<TrainingSportTrainingTypeMapping>> =
        dao.getAll()

    fun getAllByDate(fromDate: LocalDate, toDate: LocalDate) =
        dao.getAllByDate(fromDate, toDate)

    fun getSportSumsByDate(sportId: Long, fromDate: LocalDate, toDate: LocalDate) =
        dao.getSportSumsByDate(sportId, fromDate, toDate)

    fun getTrainingTypeSumsByDate(trainingTypeId: Long, fromDate: LocalDate, toDate: LocalDate) =
        dao.getTrainingTypeSumsByDate(trainingTypeId, fromDate, toDate)

}