package ch.mobpro.exercicer.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_type")
data class TrainingType(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "training_type_id")
    val id: Long? = null,
    @ColumnInfo(name = "training_type_name") var name: String
) {
    override fun toString(): String {
        return this.name
    }
}