package com.example.studentmanagement.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.studentmanagement.model.Student

@Dao
interface StudentDao {
    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<Student>

    @Insert
    suspend fun insertStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}
