package ch.mobpro.exercicer.data.util


import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import ch.mobpro.exercicer.data.entity.DistanceUnit
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import ch.mobpro.exercicer.data.entity.mapping.DateAggregationLevel
import ch.mobpro.exercicer.data.entity.mapping.ReportingData
import ch.mobpro.exercicer.data.entity.mapping.TrainingSportTrainingTypeMapping
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*


fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun getFormattedDistance(distanceInMetres: Int?, distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS): String? {
    if (distanceInMetres == null) return null
    return if (distanceUnit == DistanceUnit.KILOMETERS) {
        val distanceInKm = distanceInMetres / 1000.0
        distanceInKm.round(2)
        "$distanceInKm km"
    } else "$distanceInMetres m"
}

fun getFormattedTime(timeInSeconds: Int): String {
    val seconds = timeInSeconds % 60
    val paddedSeconds = if (seconds / 10 == 0) "0$seconds" else "$seconds"
    val minutes = (timeInSeconds / 60) % 60
    val paddedMinutes = if (minutes / 10 == 0) "0$minutes:" else "$minutes:"
    val hours = (timeInSeconds / 3600) % 3600
    val paddedHours = if (hours / 10 == 0) "0$hours:" else "$hours:"
    return "$paddedHours$paddedMinutes$paddedSeconds"
}

fun getFormattedSet(sets: Int): String {
    return "$sets sts"
}

fun getFormattedRepeats(repeats: Float): String {
    return "$repeats rep"
}

fun getFormattedWeight(weight: Float): String {
    return "$weight kg"
}

fun getFormattedTime(hours: Int, minutes: Int, seconds: Int): String {
    val sum = getTimeSum(hours, minutes, seconds)
    return getFormattedTime(sum)
}

fun getFormattedTimes(times: Int): String {
    return "$times x"
}

fun getTimeSum(hours: Int, minutes: Int, seconds: Int): Int {
    return hours * 3600 + minutes * 60 + seconds
}

fun LocalDate.getCalendarWeekString(): String {
    val calenderWeek = this.get(WeekFields.ISO.weekOfYear())
    val year = this.year
    return "$calenderWeek/$year"
}

fun LocalDate.getMonthString(): String {
    val month = this.month.toString().lowercase().replaceFirstChar { it.uppercase() }
    val year = this.year
    return "$month/$year"
}

fun LocalDate.getFormattedString(): String = this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

inline fun <reified K: Comparable<K>, reified V: Comparable<V>> List<TrainingSportTrainingTypeMapping>.groupBy(
    dateAggregationLevel: DateAggregationLevel = DateAggregationLevel.DAILY
): Map<K, Map<V, ReportingData>> {

    val resultMap = mutableMapOf<K, Map<V, ReportingData>>()
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
            val aggregate: ReportingData = trainingList.fold(ReportingData()) { leftSummingWrapper, rightMappingObject ->
                leftSummingWrapper + rightMappingObject
            }
            var aggregateMap = resultMap.getOrPut(firstLevelKey) { mutableMapOf() }.toMutableMap()
            aggregateMap[secondLevelKey] = aggregate
            resultMap[firstLevelKey] = aggregateMap
        }
    }

    return resultMap
}