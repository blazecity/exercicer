package ch.mobpro.exercicer.di

import android.content.Context
import androidx.room.Room
import ch.mobpro.exercicer.data.dao.GoalDao
import ch.mobpro.exercicer.data.dao.SportDao
import ch.mobpro.exercicer.data.dao.TrainingDao
import ch.mobpro.exercicer.data.dao.TrainingTypeDao
import ch.mobpro.exercicer.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ExercicerModule {

    @Singleton
    @Provides
    fun provideTrainingDao(database: AppDatabase): TrainingDao = database.trainingDao()

    @Singleton
    @Provides
    fun provideGoalDao(database: AppDatabase): GoalDao = database.goalDao()

    @Singleton
    @Provides
    fun provideSportDao(database: AppDatabase): SportDao = database.sportDao()

    @Singleton
    @Provides
    fun provideTrainingTypeDao(database: AppDatabase): TrainingTypeDao = database.trainingTypeDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_db").build()
    }
}