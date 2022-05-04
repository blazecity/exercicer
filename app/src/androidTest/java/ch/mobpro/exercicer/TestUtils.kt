package ch.mobpro.exercicer

import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType

fun createTestSport(sportName: String = "Gym"): Sport {
    return Sport(name = sportName, trainingType = TrainingType.STRENGTH)
}