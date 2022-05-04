package ch.mobpro.exercicer

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.dao.TrainingDao
import ch.mobpro.exercicer.data.entity.Training
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomDbTrainingTest: TestDatabase() {
    private lateinit var trainingDao: TrainingDao
    private lateinit var sportDao: SportDao

    @Before
    override fun createDb() {
        super.createDb()
        this.trainingDao = super.db.trainingDao()
        this.sportDao = super.db.sportDao()
    }

    @Test
    fun testInsert() = runTest {
        // Arrange
        val sport = createTestSport()
        val sportId = sportDao.insert(sport)
        val training = Training(date = LocalDate.now(), sportIdFkTraining = sportId)

        // Act
        trainingDao.insert(training)
        val trainingMapAfterInsertion = trainingDao.getAll().first()

        // Assert
        assertEquals(1, trainingMapAfterInsertion.size)
    }

    @Test
    fun testInsertMultipleReferenceToSameSport() = runTest {
        // Arrange
        val sport = createTestSport()
        val sportId = sportDao.insert(sport)
        val training1 = Training(date = LocalDate.now(),
                                sportIdFkTraining = sportId,
                                remarks = "Training 1")

        val training2 = Training(date = LocalDate.now().plusDays(1),
                                sportIdFkTraining = sportId,
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
        val sport = createTestSport()
        val sportId = sportDao.insert(sport)
        val training = Training(date = LocalDate.now(),
                                sportIdFkTraining = sportId,
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
        val sportGym = createTestSport()
        val sportTennis = createTestSport("Tennis")
        val sportGymId = sportDao.insert(sportGym)
        val sportTennisId = sportDao.insert(sportTennis)
        val training = Training(date = LocalDate.now(),
                                sportIdFkTraining = sportGymId,
                                remarks = "Gym exercises")

        trainingDao.insert(training)
        val trainingFromDb = trainingDao.getAll().first().keys.first()

        // Act
        trainingFromDb.sportIdFkTraining = sportTennisId
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
        val sport = createTestSport()
        val sportId = sportDao.insert(sport)
        val training = Training(date = LocalDate.now(), sportIdFkTraining = sportId)
        trainingDao.insert(training)
        val trainingFromDb = trainingDao.getAll().first().keys.first()

        // Act
        trainingDao.delete(trainingFromDb)
        val trainingMapAfterDeletion = trainingDao.getAll().first()

        // Assert
        assertEquals(0, trainingMapAfterDeletion.size)
    }
}