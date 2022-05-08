package ch.mobpro.exercicer.data.entity.mapping

import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.TrainingType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class UtilsTest {
    private lateinit var testTrainings: List<TrainingSportTrainingTypeMapping>

    @Before
    fun setupTestTrainings() {
        val trainingType1 = TrainingType(1, "Training Type 1")
        val trainingType2 = TrainingType(2, "Training Type 2")
        val sport1 = Sport(3, "Sport 1", trainingType1.id!!)
        val sport2 = Sport(4, "Sport 2", trainingType2.id!!)
        val sport3 = Sport(5, "Sport 3", trainingType2.id!!)
        val training1 = Training(6, LocalDate.now(), sport1.id!!, trainingTimeMinutes = 20, trainingTimeSeconds = 20)
        val training2 = Training(7, LocalDate.now(), sport1.id!!, trainingTimeHour = 2)
        val training3 = Training(8, LocalDate.now(), sport2.id!!, trainingTimeMinutes = 34)
        val training4 = Training(9, LocalDate.now(), sport3.id!!, trainingTimeHour = 3, trainingTimeSeconds = 4)
        this.testTrainings = listOf(
            TrainingSportTrainingTypeMapping(training1, sport1, trainingType1),
            TrainingSportTrainingTypeMapping(training2, sport1, trainingType1),
            TrainingSportTrainingTypeMapping(training3, sport2, trainingType2),
            TrainingSportTrainingTypeMapping(training4, sport3, trainingType2)
        )
    }

    @Test
    fun testGroupByTrainingTypeAndSport() {
        // Act
        val groupedResult = this.testTrainings.groupBy<TrainingType, Sport>()
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)

        // Assert
        assertEquals(2, groupedResult.size)
        assertEquals(1, subMap1.size)
        assertEquals(2, subMap2.size)
        assertEquals(8420, subMap1.values.first().sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(10804, subMap2.values.elementAt(1).sumSeconds)

    }
}