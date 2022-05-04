package ch.mobpro.exercicer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.mobpro.exercicer.data.db.AppDatabase
import org.junit.After
import org.junit.Before
import java.io.IOException

open class TestDatabase {
    protected lateinit var db: AppDatabase

    @Before
    open fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        this.db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        this.db.close()
    }
}