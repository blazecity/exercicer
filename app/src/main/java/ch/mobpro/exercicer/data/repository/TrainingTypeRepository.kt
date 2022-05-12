package ch.mobpro.exercicer.data.repository

import ch.mobpro.exercicer.data.dao.TrainingTypeDao
import ch.mobpro.exercicer.data.entity.TrainingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TrainingTypeRepository @Inject constructor(private val dao: TrainingTypeDao) {
    suspend fun insert(trainingType: TrainingType): Long = dao.insert(trainingType)
    suspend fun delete(trainingType: TrainingType) = dao.delete(trainingType)
    suspend fun update(trainingType: TrainingType) = dao.update(trainingType)
    fun getAll(): Flow<List<TrainingType>> = dao.getAll().flowOn(Dispatchers.Default).conflate()
}