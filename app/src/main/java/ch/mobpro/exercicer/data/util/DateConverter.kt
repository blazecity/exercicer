package ch.mobpro.exercicer.data.util

import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromDate(date: LocalDate): Long {
        return date.toEpochDay()
    }

    @TypeConverter
    fun toDate(epochDay: Long): LocalDate {
        return LocalDate.ofEpochDay(epochDay)
    }
}