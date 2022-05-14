package ch.mobpro.exercicer.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.mobpro.exercicer.components.Listable

@Entity(tableName = "training_type")
data class TrainingType(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "training_type_id")
    override val id: Long? = null,
    @ColumnInfo(name = "training_type_name") var name: String
): Comparable<TrainingType>, Listable {
    override fun toString(): String {
        return this.name
    }

    override fun compareTo(other: TrainingType): Int {
        return this.name.compareTo(other.name)
    }
}