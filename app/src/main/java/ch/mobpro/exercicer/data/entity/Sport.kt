package ch.mobpro.exercicer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sport(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var name: String,
    var trainingType: TrainingType
)
