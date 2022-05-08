package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.Sport
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
                "JOIN sport ON goal.sport_goal_fk = sport.sport_id"
    )
    fun getAll(): Flow<Map<Goal, Sport>>
}