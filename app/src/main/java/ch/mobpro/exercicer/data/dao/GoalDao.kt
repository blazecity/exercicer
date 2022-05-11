package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.mapping.GoalSportTrainingTypeMapping
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert
    suspend fun insert(goal: Goal): Long

    @Delete
    suspend fun delete(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Query(
        "SELECT * " +
                "FROM goal " +
                "JOIN sport ON goal.sport_goal_fk = sport.sport_id " +
                "JOIN training_type ON sport.training_type_fk = training_type.training_type_id"
    )
    fun getAll(): Flow<List<GoalSportTrainingTypeMapping>>
}