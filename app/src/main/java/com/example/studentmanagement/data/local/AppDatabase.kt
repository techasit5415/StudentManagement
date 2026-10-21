package com.example.studentmanagement.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studentmanagement.model.Student

@Database(
    entities = [Student::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}
