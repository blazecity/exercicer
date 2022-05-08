package ch.mobpro.exercicer.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Sport::class,
            parentColumns = ["sport_id"],
            childColumns = ["sport_goal_fk"]
        )
    ]
)
data class Goal(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goal_id")
    val id: Long? = null,
    @ColumnInfo(name = "sport_goal_fk") var sportId: Long,
    var start: LocalDate,
    var end: LocalDate,
    var trainingTimeGoalHours: Int? = null,
    var trainingTimeGoalMinutes: Int? = null,
    var trainingTimeGoalSeconds: Int? = null,
    var distanceGoalInMetres: Int? = null,
    var distanceUnit: DistanceUnit? = null,
    var trainingsPerWeek: Int? = null
)
