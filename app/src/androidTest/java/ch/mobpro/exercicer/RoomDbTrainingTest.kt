package ch.mobpro.exercicer

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.dao.TrainingDao
import ch.mobpro.exercicer.data.dao.TrainingTypeDao
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.getCalendarWeek
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*


@RunWith(AndroidJUnit4::class)
class RoomDbTrainingTest: TestDatabase() {
    private lateinit var trainingDao: TrainingDao
    private lateinit var sportDao: SportDao
    private lateinit var trainingTypeDao: TrainingTypeDao

    @Before
    override fun createDb() {
        super.createDb()
        this.trainingDao = super.db.trainingDao()
        this.sportDao = super.db.sportDao()
        this.trainingTypeDao = super.db.trainingTypeDao()
    }

    @Test
    fun testInsert() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = createTestSport(trainingTypeId = trainingTypeId)
        val sportId = sportDao.insert(sport)
        val training = Training(date = LocalDate.now(), sportId = sportId)

        // Act
        trainingDao.insert(training)
        val trainingMapAfterInsertion = trainingDao.getAll().first()

        // Assert
        assertEquals(1, trainingMapAfterInsertion.size)
    }

    @Test
    fun testInsertMultipleReferenceToSameSport() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = createTestSport(trainingTypeId = trainingTypeId)
        val sportId = sportDao.insert(sport)
        val training1 = Training(date = LocalDate.now(),
                                sportId = sportId,
                                remarks = "Training 1")

        val training2 = Training(date = LocalDate.now().plusDays(1),
                                sportId = sportId,
                                remarks = "Training 2")

        // Act
        trainingDao.insert(training1)
        trainingDao.insert(training2)
        val trainingMapAfterInsertion = trainingDao.getAll().first()

        // Assert
        val trainings = trainingMapAfterInsertion.keys
        val sports = trainingMapAfterInsertion.values.toSet()
        assertEquals(2, trainings.size)
        assertEquals(1, sports.size)
    }

    @Test
    fun testUpdateTraining() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = createTestSport(trainingTypeId = trainingTypeId)
        val sportId = sportDao.insert(sport)
        val training = Training(date = LocalDate.now(),
                                sportId = sportId,
                                remarks = "Before update")

        trainingDao.insert(training)
        var trainingFromDb = trainingDao.getAll().first().keys.first()

        // Act
        trainingFromDb.remarks = "After update"
        trainingDao.update(trainingFromDb)
        val trainingMapAfterUpdate = trainingDao.getAll().first()
        val trainingAfterUpdate = trainingMapAfterUpdate.keys.first()

        // Assert
        assertEquals(1, trainingMapAfterUpdate.size)
        assertEquals(trainingFromDb, trainingAfterUpdate)
    }

    @Test
    fun testUpdateReferenceToDifferentSport() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sportGym = createTestSport(trainingTypeId = trainingTypeId)
        val sportTennis = createTestSport("Tennis", trainingTypeId = trainingTypeId)
        val sportGymId = sportDao.insert(sportGym)
        val sportTennisId = sportDao.insert(sportTennis)
        val training = Training(date = LocalDate.now(),
                                sportId = sportGymId,
                                remarks = "Gym exercises")

        trainingDao.insert(training)
        val trainingFromDb = trainingDao.getAll().first().keys.first()

        // Act
        trainingFromDb.sportId = sportTennisId
        trainingFromDb.remarks = "Tennis"
        trainingDao.update(trainingFromDb)
        val trainingMapAfterUpdate = trainingDao.getAll().first()
        val trainingAfterUpdate = trainingMapAfterUpdate.keys.first()

        // Assert
        assertEquals(1, trainingMapAfterUpdate.size)
        assertEquals(trainingFromDb, trainingAfterUpdate)
    }

    @Test
    fun testDelete() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = createTestSport(trainingTypeId = trainingTypeId)
        val sportId = sportDao.insert(sport)
        val training = Training(date = LocalDate.now(), sportId = sportId)
        trainingDao.insert(training)
        val trainingFromDb = trainingDao.getAll().first().keys.first()

        // Act
        trainingDao.delete(trainingFromDb)
        val trainingMapAfterDeletion = trainingDao.getAll().first()

        // Assert
        assertEquals(0, trainingMapAfterDeletion.size)
    }

    @Test
    fun testGetAllByDate() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sportGym = createTestSport(trainingTypeId = trainingTypeId)
        val sportTennis = createTestSport("Tennis", trainingTypeId = trainingTypeId)
        val sportGymId = sportDao.insert(sportGym)
        val sportTennisId = sportDao.insert(sportTennis)
        val trainingList = listOf(
            Training(date = LocalDate.of(2022, 1, 1),
                sportId = sportGymId,
                trainingDistanceInMeters = 10),
            Training(date = LocalDate.of(2022, 1, 2),
                sportId = sportTennisId,
                trainingDistanceInMeters = 20),
            Training(date = LocalDate.of(2022, 1, 3),
                sportId = sportGymId,
                trainingDistanceInMeters = 15),
            Training(date = LocalDate.of(2022, 1, 4),
                sportId = sportTennisId,
                trainingDistanceInMeters = 40)
        )
        trainingList.forEach {
            trainingDao.insert(it)
        }

        // Act
        val filteredTrainings = trainingDao.getAllByDate(
            LocalDate.of(2022, 1, 2),
            LocalDate.of(2022, 1, 4)
        ).first()

        // SAMPLE CODE FOR AGGREGATION
        val mappingsPerTrainingType = filteredTrainings.groupBy { it.trainingType }.toMutableMap()
        val resultMap = mutableMapOf<TrainingType, Map<Sport, Int>>()
        for (trainingTypeKey in mappingsPerTrainingType.keys) {
            val mapValue = mappingsPerTrainingType[trainingTypeKey]!!
            val groupedValue = mapValue.groupBy { it.sport }
            for (sportKey in groupedValue.keys) {
                val trainingList = groupedValue[sportKey]!!
                val aggregate: Int = trainingList.fold(0) {left, right ->
                    left + right.training.trainingDistanceInMeters!!
                }
                val aggMap = resultMap.getOrPut(trainingTypeKey) { mutableMapOf() }.toMutableMap()
                aggMap[sportKey] = aggregate
            }
        }

        // Assert
        assertEquals(3, filteredTrainings.size)
    }

    @Test
    fun testGetWeekDay() {
        val today = LocalDate.now()
        assertEquals(18, today.getCalendarWeek())
    }
}