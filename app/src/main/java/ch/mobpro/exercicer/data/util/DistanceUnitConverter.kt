package ch.mobpro.exercicer.data.util

import androidx.room.TypeConverter
import ch.mobpro.exercicer.data.entity.DistanceUnit

class DistanceUnitConverter {
    @TypeConverter
    fun fromDistanceUnit(distanceUnit: DistanceUnit?): String? = distanceUnit?.name

    @TypeConverter
    fun toDistanceUnit(distanceUnitName: String?): DistanceUnit? {
        if (distanceUnitName.isNullOrEmpty()) return null
        return DistanceUnit.valueOf(distanceUnitName)
    }
}