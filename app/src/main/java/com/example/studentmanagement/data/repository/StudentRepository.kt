package com.example.studentmanagement.data.repository

import com.example.studentmanagement.data.local.StudentDao
import com.example.studentmanagement.data.remote.StudentApi
import com.example.studentmanagement.data.remote.StudentCreateRequest
import com.example.studentmanagement.model.Student

class StudentRepository(
    private val dao: StudentDao,
    private val api: StudentApi
) {
    // Returns Pair(List<Student>, isOnline: Boolean)
    suspend fun getStudents(): Pair<List<Student>, Boolean> {
        return try {
            val remoteStudents = api.getStudents().map { it.toStudent() }
            dao.deleteAll()
            dao.insertAll(remoteStudents)
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
        return api.getStudents().map { it.toStudent() }
    }

    suspend fun addStudent(student: Student) {
        api.createStudent(
            StudentCreateRequest(student.studentCode, student.name, student.major)
        )
        dao.insertStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        api.deleteStudent(student.id)
        dao.deleteStudent(student)
    }

    suspend fun updateStudent(student: Student) {
        api.updateStudent(
            student.id,
            StudentCreateRequest(student.studentCode, student.name, student.major)
        )
        dao.insertStudent(student)
    }
}
