package ch.mobpro.exercicer.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*
import kotlin.math.min

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
    var date: LocalDate,
    var sportIdFkTraining: Long,
    var trainingTimeHour: Int? = null,
    var trainingTimeMinutes: Int? = null,
    var trainingTimeSeconds: Int? = null,
    var trainingDistanceInMeters: Int? = null,
    var distanceUnit: DistanceUnit? = null,
    var sets: Int? = null,
    var repeats: Int? = null,
    var remarks: String? = null
) {
    fun getFormattedTrainingTime(): String? {
        if (trainingTimeHour == null && trainingTimeMinutes == null && trainingTimeSeconds == null)
            return null

        val hours = if (trainingTimeHour != null) "${this.trainingTimeHour}:"  else "00:"
        val minutes = if (trainingTimeMinutes != null) "${this.trainingTimeMinutes}:" else "00:"
        val seconds = if (trainingTimeSeconds != null) "${this.trainingTimeSeconds}" else "00"
        return hours + minutes + seconds
    }

    fun getFormattedTrainingDistance(): String? {
        if (trainingDistanceInMeters == null) return null
        val distUnit = if (this.distanceUnit != null) this.distanceUnit else DistanceUnit.METERS
        return "${this.trainingDistanceInMeters!! / distUnit!!.multiplicator} ${distUnit.abbrevation}"
    }

    fun getFormattedRepeatsAndSets(): String? {
        if (sets == null || repeats == null) return null
        return "${this.sets} x ${this.repeats}"
    }
}
