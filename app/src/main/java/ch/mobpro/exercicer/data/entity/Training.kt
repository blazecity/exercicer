package ch.mobpro.exercicer.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
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
    var intensity: Int? = null // soll nur bis 10 gehen
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
