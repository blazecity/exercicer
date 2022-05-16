package ch.mobpro.exercicer.data.entity.mapping


import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import java.time.LocalDate
import java.time.temporal.WeekFields

fun LocalDate.getCalendarWeek(): Int {
    return this.get(WeekFields.ISO.weekOfYear())
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
            Int::class -> when (dateAggregationLevel) {
                DateAggregationLevel.DAILY -> it.training.date
                DateAggregationLevel.WEEKLY -> it.training.date.getCalendarWeek()
                DateAggregationLevel.MONTHLY -> it.training.date.monthValue
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
            Int::class -> when (dateAggregationLevel) {
                DateAggregationLevel.DAILY -> firstLevelValue.groupBy { it.training.date }
                DateAggregationLevel.WEEKLY -> firstLevelValue.groupBy { it.training.date.getCalendarWeek() }
                DateAggregationLevel.MONTHLY -> firstLevelValue.groupBy { it.training.date.monthValue }
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