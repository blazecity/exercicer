package ch.mobpro.exercicer

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SportEntityDbTest: TestDatabase() {
    private lateinit var sportDao: SportDao

    @Before
    override fun createDb() {
        super.createDb()
        this.sportDao = super.db.sportDao()
    }

    @Test
    fun testInsert() {
        runTest {
            // Arrange
            val sport = Sport(name = "Jogging", trainingType = TrainingType.ENDURANCE)

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
        val sport = Sport(name = "Gym", trainingType = TrainingType.STRENGTH)
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
        val sport = Sport(name = "Fussball", trainingType = TrainingType.STRENGTH)
        sportDao.insert(sport)
        val sportFromDb = sportDao.getAll().first().first()

        // Act
        sportFromDb.trainingType = TrainingType.ENDURANCE
        sportDao.update(sportFromDb)
        val sportListAfterUpdate = sportDao.getAll().first()

        // Assert
        assertEquals(sportFromDb, sportListAfterUpdate.first())
        assertEquals(1, sportListAfterUpdate.size)
    }

    @Test
    fun testDelete() = runTest {
        // Arrange
        val sport = Sport(name = "Schwimmen", trainingType = TrainingType.ENDURANCE)
        sportDao.insert(sport)
        val sportFromDb = sportDao.getAll().first().first()

        // Act
        sportDao.delete(sportFromDb)
        val sportListAfterDeletion = sportDao.getAll().first()

        // Assert
        assertEquals(0, sportListAfterDeletion.size)
    }
}