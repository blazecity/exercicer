package ch.mobpro.exercicer.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TrainingType::class,
            parentColumns = ["id"],
            childColumns = ["trainingTypeId"]
        )
    ]
)
data class Sport(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    var name: String,
    var trainingTypeId: Long
)
