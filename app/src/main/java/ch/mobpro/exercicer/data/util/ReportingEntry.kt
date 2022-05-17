package ch.mobpro.exercicer.data.util

data class ReportingEntry(
    val description: String,
    val formattedDistance: String,
    val formattedTime: String,
    val sets: String,
    val repeats: String,
    val weight: String
)