package ch.mobpro.exercicer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.mobpro.exercicer.data.dao.GoalDao
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.dao.TrainingDao
import ch.mobpro.exercicer.data.entity.Goal
import ch.mobpro.exercicer.data.entity.Sport
import ch.mobpro.exercicer.data.entity.Training
import ch.mobpro.exercicer.data.util.DateConverter
import ch.mobpro.exercicer.data.util.DistanceUnitConverter
import ch.mobpro.exercicer.data.util.TrainingTypeConverter

@Database(entities = [Goal::class, Sport::class, Training::class], version = 1)
@TypeConverters(
    DateConverter::class,
    DistanceUnitConverter::class,
    TrainingTypeConverter::class,
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun goalDao(): GoalDao

    abstract fun sportDao(): SportDao

    abstract fun trainingDao(): TrainingDao

    companion object {
        private var db: AppDatabase? = null

        fun createDatabase(context: Context): AppDatabase {
            if (db == null) {
                db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "training_db"
                ).build()
            }

            return db!!
        }
    }

}