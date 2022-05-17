package ch.mobpro.exercicer

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.mobpro.exercicer.data.dao.GoalDao
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.dao.TrainingDao
import ch.mobpro.exercicer.data.dao.TrainingTypeDao
import ch.mobpro.exercicer.data.entity.*
import ch.mobpro.exercicer.data.util.getCalendarWeekString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate


@RunWith(AndroidJUnit4::class)
class RoomDbTrainingTest: TestDatabase() {
    private lateinit var trainingDao: TrainingDao
    private lateinit var sportDao: SportDao
    private lateinit var trainingTypeDao: TrainingTypeDao
    private lateinit var goalDao: GoalDao

    @Before
    override fun createDb() {
        super.createDb()
        this.trainingDao = super.db.trainingDao()
        this.sportDao = super.db.sportDao()
        this.trainingTypeDao = super.db.trainingTypeDao()
        this.goalDao = super.db.goalDao()
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
        val trainingListAfterInsertion = trainingDao.getAll().first()
        val sports = trainingListAfterInsertion.map { it.sport }.toSet()

        // Assert
        assertEquals(2, trainingListAfterInsertion.size)
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
        var trainingFromDb = trainingDao.getAll().first().first().training

        // Act
        trainingFromDb.remarks = "After update"
        trainingDao.update(trainingFromDb)
        val trainingListAfterUpdate = trainingDao.getAll().first()
        val trainingAfterUpdate = trainingListAfterUpdate.first().training

        // Assert
        assertEquals(1, trainingListAfterUpdate.size)
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
        val trainingFromDb = trainingDao.getAll().first().first().training

        // Act
        trainingFromDb.sportId = sportTennisId
        trainingFromDb.remarks = "Tennis"
        trainingDao.update(trainingFromDb)
        val trainingListAfterUpdate = trainingDao.getAll().first()
        val trainingAfterUpdate = trainingListAfterUpdate.first().training

        // Assert
        assertEquals(1, trainingListAfterUpdate.size)
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
        val trainingFromDb = trainingDao.getAll().first().first().training

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
        val date = LocalDate.of(2022, 5, 11)
        assertEquals("19/2022", date.getCalendarWeekString())
    }

    @Test
    fun testGetTrainingsByDateAndTrainingType() = runTest {
        // Arrange
        val trainingType1 = TrainingType(1, "Training Type 1")
        val trainingType2 = TrainingType(2, "Training Type 2")
        trainingTypeDao.insert(trainingType1)
        trainingTypeDao.insert(trainingType2)

        val sport1 = Sport(3, "Sport 1", trainingType1.id!!)
        val sport2 = Sport(4, "Sport 2", trainingType2.id!!)
        val sport3 = Sport(5, "Sport 3", trainingType2.id)
        sportDao.insert(sport1)
        sportDao.insert(sport2)
        sportDao.insert(sport3)

        val training1 = Training(6,
            LocalDate.of(2021, 4, 6), // 06/04/2021
            sport1.id!!,
            trainingTimeMinutes = 20,
            trainingTimeSeconds = 20)

        val training2 = Training(7,
            LocalDate.of(2022, 2, 3), // 03/02/2022
            sport1.id!!,
            trainingTimeHour = 2)

        val training3 = Training(8,
            LocalDate.of(2022, 2, 4), // 04/02/2022
            sport2.id!!,
            trainingTimeMinutes = 34)

        val training4 = Training(9,
            LocalDate.of(2022, 3, 17), // 17/03/2022
            sport3.id!!,
            trainingTimeHour = 3,
            trainingTimeSeconds = 4)

        trainingDao.insert(training1)
        trainingDao.insert(training2)
        trainingDao.insert(training3)
        trainingDao.insert(training4)

        // Act
        val sum = trainingDao.getTrainingTypeSumsByDate(
            trainingType1.id!!,
            LocalDate.of(2021, 4, 6),
            LocalDate.of(2021, 4, 6)
        ).first()

        // Assert
        assertEquals(1220, sum.sumTime)
    }
}