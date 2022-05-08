package ch.mobpro.exercicer.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TrainingType::class,
            parentColumns = ["training_type_id"],
            childColumns = ["training_type_fk"]
        )
    ]
)
data class Sport(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sport_id")
    val id: Long? = null,
    @ColumnInfo(name = "sport_name") var name: String,
    @ColumnInfo(name = "training_type_fk") var trainingTypeId: Long
)
