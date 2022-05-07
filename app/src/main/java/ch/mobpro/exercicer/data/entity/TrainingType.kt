package ch.mobpro.exercicer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_type")
data class TrainingType(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    var name: String
)