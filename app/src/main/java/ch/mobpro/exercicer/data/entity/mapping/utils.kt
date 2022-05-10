package ch.mobpro.exercicer.data.entity.mapping


import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import java.time.LocalDate
import java.time.temporal.WeekFields

fun LocalDate.getCalendarWeekString(): String {
    val calenderWeek = this.get(WeekFields.ISO.weekOfYear())
    val year = this.year
    return "$calenderWeek/$year"
}

fun LocalDate.getMonthString(): String {
    val month = this.month
    val year = this.year
    return "$month/$year"
}

inline fun <reified K, reified V> List<TrainingSportTrainingTypeMapping>.groupBy(
    dateAggregationLevel: DateAggregationLevel = DateAggregationLevel.DAILY
): Map<K, Map<V, SummingWrapper>> {

    val resultMap = mutableMapOf<K, Map<V, SummingWrapper>>()
    val firstLevelMap = this.groupBy {
        when (K::class) {
            TrainingType::class -> it.trainingType
            Sport::class -> it.sport
            LocalDate::class -> it.training.date
            String::class -> when (dateAggregationLevel) {
                DateAggregationLevel.DAILY -> it.training.date.toString()
                DateAggregationLevel.WEEKLY -> it.training.date.getCalendarWeekString()
                DateAggregationLevel.MONTHLY -> it.training.date.getMonthString()
            }
            else -> {}
        }
    } as MutableMap<K, List<TrainingSportTrainingTypeMapping>>

    for (firstLevelKey in firstLevelMap.keys) {
        val firstLevelValue = firstLevelMap[firstLevelKey]!!
        val secondLevelMap = when (V::class) {
            TrainingType::class -> firstLevelValue.groupBy { it.trainingType }
            Sport::class -> firstLevelValue.groupBy { it.sport }
            LocalDate::class -> firstLevelValue.groupBy { it.training.date }
            String::class -> when (dateAggregationLevel) {
                DateAggregationLevel.DAILY -> firstLevelValue.groupBy { it.training.date.toString() }
                DateAggregationLevel.WEEKLY -> firstLevelValue.groupBy { it.training.date.getCalendarWeekString() }
                DateAggregationLevel.MONTHLY -> firstLevelValue.groupBy { it.training.date.getMonthString() }
            }
            else -> {}
        } as MutableMap<V, List<TrainingSportTrainingTypeMapping>>

        for (secondLevelKey in secondLevelMap.keys) {
            val trainingList = secondLevelMap[secondLevelKey]!!
            val aggregate: SummingWrapper = trainingList.fold(SummingWrapper()) { leftSummingWrapper, rightMappingObject ->
                leftSummingWrapper + rightMappingObject
            }
            val aggregateMap = resultMap.getOrPut(firstLevelKey) { mutableMapOf() }.toMutableMap()
            aggregateMap[secondLevelKey] = aggregate
            resultMap[firstLevelKey] = aggregateMap
        }
    }

    return resultMap
}