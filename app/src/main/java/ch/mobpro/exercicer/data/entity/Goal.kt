package ch.mobpro.exercicer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val sportName: String,
    val start: Date,
    val end: Date,
    val trainingTimeGoalHours: Int?,
    val trainingTimeGoalMinutes: Int?,
    val trainingTimeGoalSeconds: Int?,
    val distanceGoalInMetres: Int?,
    val distanceUnit: DistanceUnit?,
    val trainingsPerWeek: Int?
)
