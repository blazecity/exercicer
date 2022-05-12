package ch.mobpro.exercicer.data.repository

import ch.mobpro.exercicer.data.dao.GoalDao
import ch.mobpro.exercicer.data.db.AppDatabase
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.mapping.GoalSportTrainingTypeMapping
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GoalRepository @Inject constructor(private val dao: GoalDao) {
    suspend fun insert(goal: Goal): Long = dao.insert(goal)
    suspend fun delete(goal: Goal) = dao.delete(goal)
    suspend fun update(goal: Goal) = dao.update(goal)
    fun getAll(): Flow<List<GoalSportTrainingTypeMapping>> = dao.getAll().flowOn(Dispatchers.Default).conflate()
}