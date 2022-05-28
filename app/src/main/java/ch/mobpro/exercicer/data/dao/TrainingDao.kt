package ch.mobpro.exercicer.data.dao

import androidx.room.*
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.mapping.AggregatedTraining
import ch.mobpro.exercicer.data.entity.mapping.ReportingData
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
                "JOIN training_type ON sport.training_type_fk = training_type.training_type_id " +
                "ORDER BY training.date desc"
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
        "SELECT (SUM(training.trainingTimeSeconds) + SUM(training.trainingTimeMinutes) * 60 + SUM(training.trainingTimeHour) * 3600) AS sumTime, " +
                "SUM(training.trainingDistanceInMeters) AS sumDistance, " +
                "COUNT(*) AS sumTimes, " +
                "MAX(training.weight) AS maxWeight " +
                "FROM training " +
                "JOIN sport ON training.sport_fk = sport.sport_id " +
                "WHERE sport.sport_id = :sportId AND training.date BETWEEN :fromDate AND :toDate"
    )
    fun getSportSumsByDate(sportId: Long, fromDate: LocalDate, toDate: LocalDate): Flow<AggregatedTraining>

    @Query(
        "SELECT (SUM(training.trainingTimeSeconds) + SUM(training.trainingTimeMinutes) * 60 + SUM(training.trainingTimeHour) * 3600) AS sumTime, " +
                "SUM(training.trainingDistanceInMeters) AS sumDistance, " +
                "COUNT(*) AS sumTimes, " +
                "MAX(training.weight) AS maxWeight " +
                "FROM training " +
                "JOIN sport ON training.sport_fk = sport.sport_id " +
                "JOIN training_type ON sport.training_type_fk = training_type.training_type_id " +
                "WHERE (training_type.training_type_id = :trainingTypeId) AND (training.date BETWEEN :fromDate AND :toDate)"
    )
    fun getTrainingTypeSumsByDate(trainingTypeId: Long, fromDate: LocalDate, toDate: LocalDate): Flow<AggregatedTraining>
}