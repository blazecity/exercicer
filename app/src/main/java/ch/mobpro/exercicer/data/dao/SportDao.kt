package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.Sport
import kotlinx.coroutines.flow.Flow

@Dao
interface SportDao {

    @Insert
    suspend fun insert(sport: Sport)

    @Delete
    suspend fun delete(sport: Sport)

    @Update
    suspend fun update(sport: Sport)

    @Query("SELECT * FROM sport")
    fun getAll(): Flow<List<Sport>>
}