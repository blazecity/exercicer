package ch.mobpro.exercicer.components

fun validateTime(
    hours: Int,
    minutes: Int,
    seconds: Int,
    validate: (Boolean, String) -> Unit
): Boolean {
    val validationResult = hours != 0 || minutes != 0 || seconds != 0
    if (!validationResult) {
        validate(validationResult, "Die Zeit darf nicht 00:00:00 sein.")
    } else validate(validationResult, "")

    return validationResult
}

fun validateAnySet(
    time: Boolean,
    distance: Boolean,
    times: Boolean,
    weight: Boolean,
    validate: (Boolean, String) -> Unit
): Boolean {
    val validationResult = time || distance || times || weight
    if (!validationResult) {
        validate(
            validationResult,
            "Mindestens etwas (Zeit, Distanz, Male, Gewicht) muss erfasst sein."
        )
    } else validate(validationResult, "")

    return validationResult
}

fun validateDistance(distance: Int, validate: (Boolean, String) -> Unit): Boolean {
    val validationResult = distance != 0
    if (!validationResult) {
        validate(validationResult, "Die Distanz darf nicht 0 sein.")
    } else validate(validationResult, "")

    return validationResult
}

fun validateTimes(times: Int, validate: (Boolean, String) -> Unit): Boolean {
    val validationResult = times != 0
    if (!validationResult) {
        validate(validationResult, "Anzahl Male darf nicht 0 sein.")
    } else validate(validationResult, "")

    return validationResult
}

fun validateWeight(weight: Float, validate: (Boolean, String) -> Unit): Boolean {
    val validationResult = weight != 0f
    if (!validationResult) {
        validate(validationResult, "Das Gewicht darf nicht 0 sein.")
    } else validate(validationResult, "")

    return validationResult
}