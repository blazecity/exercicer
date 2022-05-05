package ch.mobpro.exercicer.data.entity

enum class DistanceUnit(public val multiplicator: Int, public val abbrevation: String) {
    METERS(1, "m"),
    KILOMETERS(1000, "km")
}