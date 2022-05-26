package ch.mobpro.exercicer.data.entity

import androidx.room.*
import ch.mobpro.exercicer.components.Listable
import java.time.LocalDate

@Entity(
    indices = [
        Index(value = ["sport_goal_fk"]),
        Index(value = ["training_type_goal_fk"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Sport::class,
            parentColumns = ["sport_id"],
            childColumns = ["sport_goal_fk"]
        ),
        ForeignKey(
            entity = TrainingType::class,
            parentColumns = ["training_type_id"],
            childColumns = ["training_type_goal_fk"]
        )
    ]
)
data class Goal(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goal_id")
    override val id: Long? = null,
    @ColumnInfo(name = "sport_goal_fk") var sportId: Long? = null,
    @ColumnInfo(name = "training_type_goal_fk") var trainingTypeId: Long? = null,
    var start: LocalDate = LocalDate.now(),
    var end: LocalDate = LocalDate.now(),
    var trainingTimeGoalHours: Int = 0,
    var trainingTimeGoalMinutes: Int = 0,
    var trainingTimeGoalSeconds: Int = 0,
    var distanceGoalInMetres: Float = 0f,
    var distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    var numberOfTimesGoal: Int = 0,
    var weightGoal: Float = 0f
): Listable
