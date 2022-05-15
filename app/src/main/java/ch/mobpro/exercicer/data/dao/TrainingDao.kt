package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.mapping.SummingWrapper
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
                "JOIN sport ON training.sport_fk = sport.sport_id " +
                "JOIN training_type ON sport.training_type_fk = training_type.training_type_id"
    )
    fun getAll(): Flow<List<TrainingSportTrainingTypeMapping>>

    @Query(
        "SELECT * " +
                "FROM training " +
                "JOIN sport ON training.sport_fk = sport.sport_id " +
                "JOIN training_type ON sport.training_type_fk = training_type.training_type_id " +
                "WHERE training.date BETWEEN :fromDate AND :toDate"
    )
    fun getAllByDate(fromDate: LocalDate, toDate: LocalDate): Flow<List<TrainingSportTrainingTypeMapping>>

    @Query(
        "SELECT (SUM(COALESCE(training.trainingTimeSeconds, 0)) + SUM(COALESCE(training.trainingTimeMinutes, 0)) * 60 + SUM(COALESCE(training.trainingTimeHour, 0)) * 3600) AS sumSeconds, " +
                "SUM(training.trainingDistanceInMeters) AS sumMeters " +
                "FROM training " +
                "JOIN sport ON training.sport_fk = sport.sport_id " +
                "WHERE sport.sport_id = :sportId AND training.date BETWEEN :fromDate AND :toDate"
    )
    fun getSportSumsByDate(sportId: Long, fromDate: LocalDate, toDate: LocalDate): Flow<SummingWrapper>

    @Query(
        "SELECT (SUM(COALESCE(training.trainingTimeSeconds, 0)) + SUM(COALESCE(training.trainingTimeMinutes, 0)) * 60 + SUM(COALESCE(training.trainingTimeHour, 0)) * 3600) AS sumSeconds, " +
                "SUM(training.trainingDistanceInMeters) AS sumMeters " +
                "FROM training " +
                "JOIN sport ON training.sport_fk = sport.sport_id " +
                "JOIN training_type ON sport.training_type_fk = training_type.training_type_id " +
                "WHERE (training_type.training_type_id = :trainingTypeId) AND (training.date BETWEEN :fromDate AND :toDate)"
    )
    fun getTrainingTypeSumsByDate(trainingTypeId: Long, fromDate: LocalDate, toDate: LocalDate): Flow<SummingWrapper>
}