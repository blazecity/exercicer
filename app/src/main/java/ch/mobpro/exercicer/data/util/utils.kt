package ch.mobpro.exercicer.data.util


import ch.mobpro.exercicer.data.entity.DistanceUnit
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.DateAggregationLevel
import ch.mobpro.exercicer.data.entity.mapping.SummingWrapper
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import kotlin.math.min


fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun getFormattedDistance(distanceInMetres: Int?, distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS): String? {
    if (distanceInMetres == null) return null
    if (distanceUnit == DistanceUnit.KILOMETERS) {
        val distanceInKm = distanceInMetres / 1000.0
        distanceInKm.round(2)
        return "$distanceInKm km"
    } else return "$distanceInMetres m"
}

fun getFormattedTime(timeInSeconds: Int): String {
    val seconds = timeInSeconds % 60
    val paddedSeconds = if (seconds / 10 == 0) "0$seconds" else "$seconds"
    val minutes = (timeInSeconds / 60) % 60
    val paddedMinutes = if (minutes / 10 == 0) "0$minutes:" else "$minutes:"
    val hours = (timeInSeconds / 3600)
    val paddedHours = if (hours / 10 == 0) "0$hours:" else "$hours"
    return "$paddedHours$paddedMinutes$paddedSeconds"
}

fun getFormattedTime(hours: Int?, minutes: Int?, seconds: Int?): String? {
    val sum = getTimeSum(hours, minutes, seconds) ?: return null
    return getFormattedTime(sum)
}

fun getTimeSum(hours: Int?, minutes: Int?, seconds: Int?): Int? {
    if (hours == null && minutes == null && seconds == null) return null
    return (hours ?: 0) * 3600 + (minutes ?: 0) * 60 + (seconds ?: 0)
}

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

fun LocalDate.getFormattedString(): String = this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

inline fun <reified K: Comparable<K>, reified V: Comparable<V>> List<TrainingSportTrainingTypeMapping>.groupBy(
    dateAggregationLevel: DateAggregationLevel = DateAggregationLevel.DAILY
): Map<K, Map<V, SummingWrapper>> {

    val resultMap = mutableMapOf<K, Map<V, SummingWrapper>>()
    var firstLevelMap = this.groupBy {
        when (K::class) {
            TrainingType::class -> it.trainingType
            Sport::class -> it.sport
            LocalDate::class -> it.training.date
            String::class -> when (dateAggregationLevel) {
                DateAggregationLevel.DAILY -> it.training.date.getFormattedString()
                DateAggregationLevel.WEEKLY -> it.training.date.getCalendarWeekString()
                DateAggregationLevel.MONTHLY -> it.training.date.getMonthString()
            }
            else -> {}
        }
    } as MutableMap<K, List<TrainingSportTrainingTypeMapping>>

    firstLevelMap = firstLevelMap.toSortedMap()

    for (firstLevelKey in firstLevelMap.keys) {
        val firstLevelValue = firstLevelMap[firstLevelKey]!!
        var secondLevelMap = when (V::class) {
            TrainingType::class -> firstLevelValue.groupBy { it.trainingType }
            Sport::class -> firstLevelValue.groupBy { it.sport }
            LocalDate::class -> firstLevelValue.groupBy { it.training.date }
            String::class -> when (dateAggregationLevel) {
                DateAggregationLevel.DAILY -> firstLevelValue.groupBy { it.training.date.getFormattedString() }
                DateAggregationLevel.WEEKLY -> firstLevelValue.groupBy { it.training.date.getCalendarWeekString() }
                DateAggregationLevel.MONTHLY -> firstLevelValue.groupBy { it.training.date.getMonthString() }
            }
            else -> {}
        } as MutableMap<V, List<TrainingSportTrainingTypeMapping>>

        secondLevelMap = secondLevelMap.toSortedMap()

        for (secondLevelKey in secondLevelMap.keys) {
            val trainingList = secondLevelMap[secondLevelKey]!!
            val aggregate: SummingWrapper = trainingList.fold(SummingWrapper()) { leftSummingWrapper, rightMappingObject ->
                leftSummingWrapper + rightMappingObject
            }
            var aggregateMap = resultMap.getOrPut(firstLevelKey) { mutableMapOf() }.toMutableMap()
            aggregateMap[secondLevelKey] = aggregate
            resultMap[firstLevelKey] = aggregateMap
        }
    }

    return resultMap
}