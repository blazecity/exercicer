package ch.mobpro.exercicer.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Sport::class,
            parentColumns = ["sportId"],
            childColumns = ["sportIdFkTraining"]
        )
    ]
)
data class Training(
    @PrimaryKey(autoGenerate = true) val trainingId: Long? = null,
    var date: Date,
    var sportIdFkTraining: Long,
    var trainingTimeHour: Int? = null,
    var trainingTimeMinutes: Int? = null,
    var trainingTimeSeconds: Int? = null,
    var trainingDistanceInMeters: Int? = null,
    var distanceUnit: DistanceUnit? = null,
    var sets: Int? = null,
    var repeats: Int? = null,
    var remarks: String? = null
)
