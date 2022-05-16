package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SportDao {

    @Insert
    suspend fun insert(sport: Sport): Long

    @Delete
    suspend fun delete(sport: Sport)

    @Update
    suspend fun update(sport: Sport)

    @Query("SELECT * FROM sport")
    fun getAll(): Flow<List<Sport>>

    @Query("SELECT * " +
            "FROM sport " +
            "JOIN training_type ON sport.training_type_fk = training_type.training_type_id"
    )
    fun getAllJoined(): Flow<Map<Sport, TrainingType>>
}