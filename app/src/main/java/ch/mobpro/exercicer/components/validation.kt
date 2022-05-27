package ch.mobpro.exercicer.components

import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import java.time.LocalDate

enum class ValidationEntry(val message: String) {
    DATE_PAIR("Das Startdatum muss kleiner sein als das Enddatum"),
    TIME("Die Zeit darf nicht 00:00:00 sein."),
    DISTANCE("Die Distanz darf nicht 0 sein."),
    NUMBER_OF_TIMES("Die Anzahl Male muss grösser als 0 sein."),
    WEIGHT("Das Gewicht darf nicht 0 sein."),
    ANY_SET("Mindestens ein Ziel muss erfasst sein."),
    TRAINING_TYPE("Bitte einen gültigen Trainingstyp wählen"),
    SPORT("Bitte eine gültige Sportart wählen.")
}

class Validation {
    private val validationEntries = mutableListOf<ValidationEntry>()

    fun addValidationEntry(entry: ValidationEntry) {
        validationEntries.add(entry)
    }

    fun removeValidationEntry(entry: ValidationEntry) {
        validationEntries.remove(entry)
    }

    fun validate(): Boolean {
        return validationEntries.isEmpty()
    }

    fun getFirstEntryMessage(): String? {
        if (validationEntries.isEmpty()) return null
        return validationEntries.first().message
    }
}

fun validateTime(
    hours: Int,
    minutes: Int,
    seconds: Int,
    validation: Validation
): Boolean {
    val validationResult = (hours >= 0 && minutes >= 0 && seconds >= 0) && (hours > 0 || minutes > 0 || seconds > 0)
    if (!validationResult) {
        validation.addValidationEntry(ValidationEntry.TIME)
    } else validation.removeValidationEntry(ValidationEntry.TIME)

    return validationResult
}

fun validateAnySet(
    time: Boolean,
    distance: Boolean,
    times: Boolean,
    weight: Boolean,
    validation: Validation
): Boolean {
    val validationResult = time || distance || times || weight
    if (!validationResult) {
        validation.addValidationEntry(ValidationEntry.ANY_SET)
    } else validation.removeValidationEntry(ValidationEntry.ANY_SET)

    return validationResult
}

fun validateDistance(distance: Float, validation: Validation): Boolean {
    val validationResult = distance != 0f
    if (!validationResult) {
        validation.addValidationEntry(ValidationEntry.DISTANCE)
    } else validation.removeValidationEntry(ValidationEntry.DISTANCE)

    return validationResult
}

fun validateTimes(times: Int, validation: Validation): Boolean {
    val validationResult = times != 0
    if (!validationResult) {
        validation.addValidationEntry(ValidationEntry.NUMBER_OF_TIMES)
    } else validation.removeValidationEntry(ValidationEntry.NUMBER_OF_TIMES)

    return validationResult
}

fun validateWeight(weight: Float, validation: Validation): Boolean {
    val validationResult = weight != 0f
    if (!validationResult) {
        validation.addValidationEntry(ValidationEntry.WEIGHT)
    } else validation.removeValidationEntry(ValidationEntry.WEIGHT)

    return validationResult
}

fun validateTrainingType(trainingType: TrainingType, validation: Validation): Boolean {
    val validationResult = trainingType.id != null
    if (!validationResult) {
        validation.addValidationEntry(ValidationEntry.TRAINING_TYPE)
    } else validation.removeValidationEntry(ValidationEntry.TRAINING_TYPE)

    return validationResult
}

fun validateSport(sport: Sport, validation: Validation): Boolean {
    val validationResult = sport.id != null
    if (!validationResult) {
        validation.addValidationEntry(ValidationEntry.SPORT)
    } else validation.removeValidationEntry(ValidationEntry.SPORT)

    return validationResult
}

fun validateDatePair(fromDate: LocalDate, toDate: LocalDate, validation: Validation): Boolean {
    val validationResult = fromDate <= toDate
    if (!validationResult) {
        validation.addValidationEntry(ValidationEntry.DATE_PAIR)
    } else validation.removeValidationEntry(ValidationEntry.DATE_PAIR)

    return validationResult
}