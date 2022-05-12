package ch.mobpro.exercicer.data.entity.mapping

import androidx.room.Embedded
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType

class GoalSportTrainingTypeMapping(
    @Embedded val goal: Goal,
    @Embedded val sport: Sport,
    @Embedded val trainingType: TrainingType
)