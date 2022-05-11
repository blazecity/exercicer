package ch.mobpro.exercicer.data.repository

import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.entity.Sport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SportRepository @Inject constructor(private val dao: SportDao) {
    suspend fun insert(sport: Sport): Long = dao.insert(sport)
    suspend fun delete(sport: Sport) = dao.delete(sport)
    suspend fun update(sport: Sport) = dao.update(sport)
    fun getAll(): Flow<List<Sport>> = dao.getAll().flowOn(Dispatchers.Default).conflate()
}