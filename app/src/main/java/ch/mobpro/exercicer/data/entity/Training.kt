package ch.mobpro.exercicer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Training(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val date: Date,
    val sportName: String,
    val trainingTimeHour: Int?,
    val trainingTimeMinutes: Int?,
    val trainingTimeSeconds: Int?,
    val trainingDistanceInMeters: Int?,
    val distanceUnit: DistanceUnit?,
    val sets: Int?,
    val repeats: Int?,
    val remarks: String?
)
