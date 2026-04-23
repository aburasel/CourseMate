package com.amr.coursemate.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amr.coursemate.data.dao.CourseClassDao
import com.amr.coursemate.data.dao.TranslationDao
import com.amr.coursemate.data.model.CourseClass
import com.amr.coursemate.data.model.Translation

@Database(entities = [CourseClass::class, Translation::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun courseClassDao(): CourseClassDao
    abstract fun translationDao(): TranslationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coursemate.db"
                ).build().also { INSTANCE = it }
            }
    }
}