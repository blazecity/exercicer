package ch.mobpro.exercicer.data.entity

import ch.mobpro.exercicer.components.Listable

enum class DistanceUnit(
    override val id: Long?,
    val multiplicator: Int,
    private val abbrevation: String
): Listable {
    METERS(0,  1, "m"),
    KILOMETERS(1, 1000, "km");

    override fun toString(): String {
        return this.abbrevation
    }
}