package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    @Insert
    suspend fun insert(training: Training)

    @Delete
    suspend fun delete(training: Training)

    @Update
    suspend fun update(training: Training)

    @Query(
        "SELECT * " +
                "FROM training " +
                "JOIN sport ON training.sportName = sport.name"
    )
    fun getAll(): Flow<Map<Training, Sport>>
}