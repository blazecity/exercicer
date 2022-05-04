package ch.mobpro.exercicer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.db.AppDatabase
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SportEntityDbTest {
    private lateinit var db: AppDatabase
    private lateinit var sportDao: SportDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        this.db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        this.sportDao = this.db.sportDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        this.db.close()
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
            assertEquals("Jogging", sportsFromDb.first().name)
            assertEquals(1, sportsFromDb.size)
        }
    }

    @Test
    fun testUpdateName() = runTest {
        // Arrange
        val sport = Sport(name = "Gym", trainingType = TrainingType.STRENGTH)

        // Act
        sportDao.insert(sport)
        val sportFromDb = sportDao.getAll().first().first()
        sportFromDb.name = "Gym (Oberkörper)"
        sportDao.update(sportFromDb)
        val sportListAfterUpdate = sportDao.getAll().first()

        // Assert
        assertEquals("Gym (Oberkörper)", sportListAfterUpdate.first().name)
        assertEquals(1, sportListAfterUpdate.size)
    }

    @Test
    fun testUpdateTrainingType() = runTest {
        // Arrange
        val sport = Sport(name = "Fussball", trainingType = TrainingType.STRENGTH)

        // Act
        sportDao.insert(sport)
        val sportFromDb = sportDao.getAll().first().first()
        sportFromDb.trainingType = TrainingType.ENDURANCE
        sportDao.update(sportFromDb)
        val sportListAfterUpdate = sportDao.getAll().first()

        // Assert
        assertEquals(TrainingType.ENDURANCE, sportListAfterUpdate.first().trainingType)
        assertEquals(1, sportListAfterUpdate.size)
    }

    @Test
    fun testDelete() = runTest {
        // Arrange
        val sport = Sport(name = "Schwimmen", trainingType = TrainingType.ENDURANCE)

        // Act
        sportDao.insert(sport)
        val sportFromDb = sportDao.getAll().first().first()
        sportDao.delete(sportFromDb)
        val sportListAfterDeletion = sportDao.getAll().first()

        // Assert
        assertEquals(0, sportListAfterDeletion.size)
    }
}