package ch.mobpro.exercicer.data.util

import androidx.room.TypeConverter
import ch.mobpro.exercicer.data.entity.TrainingType

class TrainingTypeConverter {
    @TypeConverter
    fun fromTrainingType(trainingType: TrainingType?): String? = trainingType?.name

    @TypeConverter
    fun toTrainingType(trainingTypeName: String): TrainingType? {
        if (trainingTypeName.isNullOrEmpty()) return null
        return TrainingType.valueOf(trainingTypeName)
    }
}