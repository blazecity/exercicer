package ch.mobpro.exercicer.data.entity

import androidx.room.*
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
    val id: Long? = null,
    var date: LocalDate,
    @ColumnInfo(name = "sport_fk") var sportId: Long,
    var trainingTimeHour: Int? = null,
    var trainingTimeMinutes: Int? = null,
    var trainingTimeSeconds: Int? = null,
    var trainingDistanceInMeters: Int? = null,
    var distanceUnit: DistanceUnit? = null,
    var sets: Int? = null,
    var repeats: Int? = null,
    var remarks: String? = null,
    var intensity: Int? = null, // soll nur bis 10 gehen
    var comment: String? = null
)

