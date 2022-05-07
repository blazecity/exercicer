package ch.mobpro.exercicer

import ch.mobpro.exercicer.data.dao.GoalDao
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.dao.TrainingTypeDao
import ch.mobpro.exercicer.data.entity.TrainingType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class RoomDbGoalTest: TestDatabase() {
    private lateinit var goalDao: GoalDao
    private lateinit var sportDao: SportDao
    private lateinit var trainingTypeDao: TrainingTypeDao

    @Before
    override fun createDb() {
        super.createDb()
        this.goalDao = super.db.goalDao()
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
        val goal = createTestGoal(sportId)

        // Act
        goalDao.insert(goal)
        val goalMapAfterInsertion = goalDao.getAll().first()

        // Assert
        Assert.assertEquals(1, goalMapAfterInsertion.size)
    }

    @Test
    fun testInsertMultipleReferenceToSameSport() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = createTestSport(trainingTypeId = trainingTypeId)
        val sportId = sportDao.insert(sport)
        val goal1 = createTestGoal(sportId)
        val goal2 = createTestGoal(sportId)

        // Act
        goalDao.insert(goal1)
        goalDao.insert(goal2)
        val goalMapAfterInsertion = goalDao.getAll().first()

        // Assert
        val goals = goalMapAfterInsertion.keys
        val sports = goalMapAfterInsertion.values.toSet()
        Assert.assertEquals(2, goals.size)
        Assert.assertEquals(1, sports.size)
    }

    @Test
    fun testUpdateGoal() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = createTestSport(trainingTypeId = trainingTypeId)
        val sportId = sportDao.insert(sport)
        val goal = createTestGoal(sportId)
        goalDao.insert(goal)
        var goalFromDb = goalDao.getAll().first().keys.first()

        // Act
        goalFromDb.distanceGoalInMetres = 1000
        goalDao.update(goalFromDb)
        val goalMapAfterUpdate = goalDao.getAll().first()
        val goalAfterUpdate = goalMapAfterUpdate.keys.first()

        // Assert
        Assert.assertEquals(1, goalMapAfterUpdate.size)
        Assert.assertEquals(goalFromDb, goalAfterUpdate)
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
        val goal = createTestGoal(sportGymId)
        goalDao.insert(goal)
        val goalFromDb = goalDao.getAll().first().keys.first()

        // Act
        goalFromDb.sportId = sportTennisId
        goalDao.update(goalFromDb)
        val goalMapAfterUpdate = goalDao.getAll().first()
        val goalAfterUpdate = goalMapAfterUpdate.keys.first()

        // Assert
        Assert.assertEquals(1, goalMapAfterUpdate.size)
        Assert.assertEquals(goalFromDb, goalAfterUpdate)
    }

    @Test
    fun testDelete() = runTest {
        // Arrange
        val trainingType = createTestTrainingType()
        val trainingTypeId = trainingTypeDao.insert(trainingType)
        val sport = createTestSport(trainingTypeId = trainingTypeId)
        val sportId = sportDao.insert(sport)
        val goal = createTestGoal(sportId)
        goalDao.insert(goal)
        val goalFromDb = goalDao.getAll().first().keys.first()

        // Act
        goalDao.delete(goalFromDb)
        val goalMapAfterDeletion = goalDao.getAll().first()

        // Assert
        Assert.assertEquals(0, goalMapAfterDeletion.size)
    }
}