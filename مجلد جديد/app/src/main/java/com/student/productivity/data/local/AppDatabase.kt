package com.student.productivity.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.student.productivity.data.local.dao.AppDao
import com.student.productivity.data.local.entity.AssignmentOrExamEntity
import com.student.productivity.data.local.entity.ChapterEntity
import com.student.productivity.data.local.entity.SemesterEntity

@Database(
    entities = [
        SemesterEntity::class,
        ChapterEntity::class,
        AssignmentOrExamEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_productivity_database"
                )
                // Fallback to destructive migration is fine for a productivity starter app,
                // but in production, real migrations are preferred.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
