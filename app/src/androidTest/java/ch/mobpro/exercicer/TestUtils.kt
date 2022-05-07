package ch.mobpro.exercicer


import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import java.time.LocalDate

fun createTestSport(sportName: String = "Gym", trainingTypeId: Long): Sport =
    Sport(name = sportName, trainingTypeId = trainingTypeId)

fun createTestGoal(sportId: Long): Goal =
    Goal(sportId = sportId, start = getLocalDateNow(), end = getLocalDateTomorrow())

fun createTestTrainingType(name: String = "Endurance"): TrainingType = TrainingType(name = name)

fun getLocalDateNow(): LocalDate = LocalDate.now()

fun getLocalDateTomorrow(): LocalDate = LocalDate.now().plusDays(1)