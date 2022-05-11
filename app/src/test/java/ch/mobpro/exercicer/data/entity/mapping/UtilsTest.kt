package ch.mobpro.exercicer.data.entity.mapping

import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.util.groupBy
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class UtilsTest {
    private lateinit var testTrainings: List<TrainingSportTrainingTypeMapping>

    @Before
    fun setupTestTrainings() {
        val trainingType1 = TrainingType(1, "Training Type 1")
        val trainingType2 = TrainingType(2, "Training Type 2")

        val sport1 = Sport(3, "Sport 1", trainingType1.id!!)
        val sport2 = Sport(4, "Sport 2", trainingType2.id!!)
        val sport3 = Sport(5, "Sport 3", trainingType2.id!!)

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

        assertEquals(8420, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(10804, subMap2.values.elementAt(1).sumSeconds)
    }

    @Test
    fun testGroupBySportAndTrainingType() {
        // Act
        val groupedResult = this.testTrainings.groupBy<Sport, TrainingType>()
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)
        val subMap3 = groupedResult.values.elementAt(2)

        // Assert
        assertEquals(3, groupedResult.size)
        assertEquals(1, subMap1.size)
        assertEquals(1, subMap2.size)
        assertEquals(1, subMap3.size)

        assertEquals(8420, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(10804, subMap3.values.elementAt(0).sumSeconds)
    }

    @Test
    fun testGroupByTrainingTypeAndDateDaily() {
        // Act
        val groupedResult = this.testTrainings.groupBy<TrainingType, LocalDate>()
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)

        // Assert
        assertEquals(2, groupedResult.size)
        assertEquals(2, subMap1.size)
        assertEquals(2, subMap2.size)

        assertEquals(1220, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(7200, subMap1.values.elementAt(1).sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(10804, subMap2.values.elementAt(1).sumSeconds)
    }

    @Test
    fun testGroupByTrainingTypeAndDateWeekly() {
        // Act
        val groupedResult = this.testTrainings.groupBy<TrainingType, String>(DateAggregationLevel.WEEKLY)
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)

        // Assert
        assertEquals(2, groupedResult.size)
        assertEquals(2, subMap1.size)
        assertEquals(2, subMap2.size)

        assertEquals(1220, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(7200, subMap1.values.elementAt(1).sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(10804, subMap2.values.elementAt(1).sumSeconds)
    }

    @Test
    fun testGroupByTrainingTypeAndDateMonthly() {
        // Act
        val groupedResult = this.testTrainings.groupBy<TrainingType, String>(DateAggregationLevel.MONTHLY)
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)

        // Assert
        assertEquals(2, groupedResult.size)
        assertEquals(2, subMap1.size)
        assertEquals(2, subMap2.size)

        assertEquals(1220, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(7200, subMap1.values.elementAt(1).sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(10804, subMap2.values.elementAt(1).sumSeconds)
    }

    @Test
    fun testGroupByDateDailyAndTrainingType() {
        // Act
        val groupedResult = this.testTrainings.groupBy<LocalDate, TrainingType>()
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)
        val subMap3 = groupedResult.values.elementAt(2)
        val subMap4 = groupedResult.values.elementAt(3)

        // Assert
        assertEquals(4, groupedResult.size)
        assertEquals(1, subMap1.size)
        assertEquals(1, subMap2.size)
        assertEquals(1, subMap3.size)
        assertEquals(1, subMap4.size)

        assertEquals(1220, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(7200, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(2040, subMap3.values.elementAt(0).sumSeconds)
        assertEquals(10804, subMap4.values.elementAt(0).sumSeconds)
    }

    @Test
    fun testGroupByDateWeeklyAndTrainingType() {
        // Act
        val groupedResult = this.testTrainings.groupBy<String, TrainingType>(DateAggregationLevel.WEEKLY)
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)
        val subMap3 = groupedResult.values.elementAt(2)

        // Assert
        assertEquals(3, groupedResult.size)
        assertEquals(1, subMap1.size)
        assertEquals(2, subMap2.size)
        assertEquals(1, subMap3.size)

        assertEquals(1220, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(7200, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(1).sumSeconds)
        assertEquals(10804, subMap3.values.elementAt(0).sumSeconds)
    }

    @Test
    fun testGroupByDateMonthlyAndTrainingType() {
        // Act
        val groupedResult = this.testTrainings.groupBy<String, TrainingType>(DateAggregationLevel.MONTHLY)
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)
        val subMap3 = groupedResult.values.elementAt(2)

        // Assert
        assertEquals(3, groupedResult.size)
        assertEquals(1, subMap1.size)
        assertEquals(2, subMap2.size)
        assertEquals(1, subMap3.size)

        assertEquals(1220, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(7200, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(1).sumSeconds)
        assertEquals(10804, subMap3.values.elementAt(0).sumSeconds)
    }

    @Test
    fun testGroupBySportAndDateWeekly() {
        // Act
        val groupedResult = this.testTrainings.groupBy<String, Sport>(DateAggregationLevel.WEEKLY)
        val subMap1 = groupedResult.values.elementAt(0)
        val subMap2 = groupedResult.values.elementAt(1)
        val subMap3 = groupedResult.values.elementAt(2)

        // Assert
        assertEquals(3, groupedResult.size)
        assertEquals(1, subMap1.size)
        assertEquals(2, subMap2.size)
        assertEquals(1, subMap3.size)

        assertEquals(1220, subMap1.values.elementAt(0).sumSeconds)
        assertEquals(7200, subMap2.values.elementAt(0).sumSeconds)
        assertEquals(2040, subMap2.values.elementAt(1).sumSeconds)
        assertEquals(10804, subMap3.values.elementAt(0).sumSeconds)
    }
}