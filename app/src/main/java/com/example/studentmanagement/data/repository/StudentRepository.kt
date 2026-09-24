package com.example.studentmanagement.data.repository

import com.example.studentmanagement.data.local.StudentDao
import com.example.studentmanagement.data.remote.StudentApi
import com.example.studentmanagement.model.Student

class StudentRepository(
    private val dao: StudentDao,
    private val api: StudentApi
) {
    // Returns Pair(List<Student>, isOnline: Boolean)
    suspend fun getStudents(): Pair<List<Student>, Boolean> {
        return try {
            val remoteStudents = api.getStudents()
            remoteStudents.forEach { student ->
                dao.insertStudent(student)
            }
            Pair(remoteStudents, true)
        } catch (e: Exception) {
            var localStudents = dao.getAllStudents()
            if (localStudents.isEmpty()) {
                val defaultStudents = listOf(
                    Student(1, "66050160", "Techasit", "Computer Science"),
                    Student(2, "65010002", "Suda", "Information Technology"),
                    Student(3, "65010003", "Mana", "Digital Technology")
                )
                defaultStudents.forEach { dao.insertStudent(it) }
                localStudents = defaultStudents
            }
            Pair(localStudents, false)
        }
    }

    suspend fun getLocalStudents(): List<Student> {
        return dao.getAllStudents()
    }

    suspend fun getRemoteStudents(): List<Student> {
        return api.getStudents()
    }

    suspend fun addStudent(student: Student) {
        dao.insertStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        dao.deleteStudent(student)
    }
}
