package ch.mobpro.exercicer.data.entity

import androidx.room.*
import ch.mobpro.exercicer.components.Listable
import java.time.LocalDate

@Entity(
    indices = [
        Index(value = ["sport_fk"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Sport::class,
            parentColumns = ["sport_id"],
            childColumns = ["sport_fk"]
        )
    ]
)
data class Training(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "training_id")
    override val id: Long? = null,
    var date: LocalDate,
    @ColumnInfo(name = "sport_fk") var sportId: Long,
    var trainingTimeHour: Int = 0,
    var trainingTimeMinutes: Int = 0,
    var trainingTimeSeconds: Int = 0,
    var trainingDistanceInMeters: Float = 0f,
    var distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    var sets: Int = 0,
    var repeats: Int = 0,
    var weight: Float = 0f,
    var intensity: Int = 0,
    var comment: String = ""
) : Listable

