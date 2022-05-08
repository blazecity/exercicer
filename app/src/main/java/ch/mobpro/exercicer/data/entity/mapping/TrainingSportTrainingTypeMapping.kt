package ch.mobpro.exercicer.data.entity.mapping

import androidx.room.Embedded
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.entity.TrainingType

data class TrainingSportTrainingTypeMapping(
    @Embedded val training: Training,
    @Embedded val sport: Sport,
    @Embedded val trainingType: TrainingType
)
