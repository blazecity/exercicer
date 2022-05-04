package ch.mobpro.exercicer

import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.TrainingType
import java.time.LocalDate

fun createTestSport(sportName: String = "Gym"): Sport =
    Sport(name = sportName, trainingType = TrainingType.STRENGTH)

fun createTestGoal(sportId: Long): Goal =
    Goal(sportIdFkGoal = sportId, start = getLocalDateNow(), end = getLocalDateTomorrow())

fun getLocalDateNow(): LocalDate = LocalDate.now()

fun getLocalDateTomorrow(): LocalDate = LocalDate.now().plusDays(1)