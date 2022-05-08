package ch.mobpro.exercicer

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.dao.TrainingTypeDao
import ch.mobpro.exercicer.data.entity.Sport
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SportEntityDbTest: TestDatabase() {
    private lateinit var sportDao: SportDao
    private lateinit var trainingTypeDao: TrainingTypeDao

    @Before
    override fun createDb() {
        super.createDb()
        this.sportDao = super.db.sportDao()
        this.trainingTypeDao = super.db.trainingTypeDao()
    }

    @Test
    fun testInsert() {
        runTest {
            // Arrange
            val trainingType = createTestTrainingType()
            val trainingTypeId = trainingTypeDao.insert(trainingType)
            val sport = Sport(name = "Jogging", trainingTypeId = trainingTypeId)

            // Act
            sportDao.insert(sport)
            var sportsFromDb = sportDao.getAll().first()

            // Assert
            assertEquals(1, sportsFromDb.size)
        }
    }

    @Test
    fun testUpdateName() = runTest {
        // Arrange
        val trainingType = createTestTrainingType("Strength")
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = Sport(name = "Gym", trainingTypeId = trainingTypeId)
        sportDao.insert(sport)
        val sportFromDb = sportDao.getAll().first().first()

        // Act
        sportFromDb.name = "Gym (Oberkörper)"
        sportDao.update(sportFromDb)
        val sportListAfterUpdate = sportDao.getAll().first()

        // Assert
        assertEquals(sportFromDb, sportListAfterUpdate.first())
        assertEquals(1, sportListAfterUpdate.size)
    }

    @Test
    fun testUpdateTrainingType() = runTest {
        // Arrange
        val trainingTypeStrength = createTestTrainingType("Strength")
        val trainingTypeStrengthId = trainingTypeDao.insert(trainingTypeStrength)
        val sport = Sport(name = "Fussball", trainingTypeId = trainingTypeStrengthId)
        sportDao.insert(sport)
        val sportFromDb = sportDao.getAll().first().first()

        // Act
        val trainingTypeEndurance = createTestTrainingType("Endurance")
        val trainingTypeEnduranceId = trainingTypeDao.insert(trainingTypeEndurance)
        sportFromDb.trainingTypeId = trainingTypeEnduranceId
        sportDao.update(sportFromDb)
        val sportListAfterUpdate = sportDao.getAll().first()

        // Assert
        assertEquals(sportFromDb, sportListAfterUpdate.first())
        assertEquals(1, sportListAfterUpdate.size)
    }

    @Test
    fun testDelete() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = Sport(name = "Schwimmen", trainingTypeId = trainingTypeId)
        sportDao.insert(sport)
        val sportFromDb = sportDao.getAll().first().first()

        // Act
        sportDao.delete(sportFromDb)
        val sportListAfterDeletion = sportDao.getAll().first()

        // Assert
        assertEquals(0, sportListAfterDeletion.size)
    }
}