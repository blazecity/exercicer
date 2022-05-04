package ch.mobpro.exercicer.data.util

import androidx.room.TypeConverter
import ch.mobpro.exercicer.data.entity.TrainingType

class TrainingTypeConverter {
    @TypeConverter
    fun fromTrainingType(trainingType: TrainingType): String = trainingType.name

    @TypeConverter
    fun toTrainingType(trainingTypeName: String): TrainingType = TrainingType.valueOf(trainingTypeName)
}