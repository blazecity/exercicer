package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    @Insert
    suspend fun insert(training: Training): Long

    @Delete
    suspend fun delete(training: Training)

    @Update
    suspend fun update(training: Training)

    @Query(
        "SELECT * " +
                "FROM training " +
                "JOIN sport ON training.sportIdFkTraining = sport.sportId"
    )
    fun getAll(): Flow<Map<Training, Sport>>
}