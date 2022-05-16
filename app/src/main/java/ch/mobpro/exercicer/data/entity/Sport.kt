package ch.mobpro.exercicer.data.entity

import androidx.room.*
import ch.mobpro.exercicer.components.Listable

@Entity(
    indices = [
        Index(value = ["training_type_fk"])
    ],
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
    override val id: Long? = null,
    @ColumnInfo(name = "sport_name") var name: String,
    @ColumnInfo(name = "training_type_fk") var trainingTypeId: Long? = null
): Comparable<Sport>, Listable {
    override fun toString(): String {
        return this.name
    }

    override fun compareTo(other: Sport): Int {
        return this.name.compareTo(other.name)
    }
}
