package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.TrainingType
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingTypeDao {
    @Insert
    suspend fun insert(trainingType: TrainingType): Long

    @Delete
    suspend fun delete(trainingType: TrainingType)

    @Update
    suspend fun update(trainingType: TrainingType)

    @Query("SELECT * FROM training_type")
    fun getAll(): Flow<List<TrainingType>>
}