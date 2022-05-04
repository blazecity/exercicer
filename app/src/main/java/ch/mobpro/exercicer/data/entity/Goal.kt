package ch.mobpro.exercicer.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Sport::class,
            parentColumns = ["sportId"],
            childColumns = ["sportIdFkGoal"]
        )
    ]
)
data class Goal(
    @PrimaryKey(autoGenerate = true) val goalId: Long? = null,
    var sportIdFkGoal: Long,
    var start: LocalDate,
    var end: LocalDate,
    var trainingTimeGoalHours: Int? = null,
    var trainingTimeGoalMinutes: Int? = null,
    var trainingTimeGoalSeconds: Int? = null,
    var distanceGoalInMetres: Int? = null,
    var distanceUnit: DistanceUnit? = null,
    var trainingsPerWeek: Int? = null
)
