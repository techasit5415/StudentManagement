package com.example.studentmanagement.data.repository

import com.example.studentmanagement.data.local.StudentDao
import com.example.studentmanagement.data.remote.StudentApi
import com.example.studentmanagement.data.remote.StudentCreateRequest
import com.example.studentmanagement.model.Student
import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val dao: StudentDao,
    private val api: StudentApi
) {
    fun observeStudents(): Flow<List<Student>> = dao.observeStudents()

    suspend fun refreshFromServer(): Boolean {
        return try {
            api.getStudents().forEach { dto ->
                val existing = dao.getByServerId(dto.id)
                dao.insertStudent(dto.toStudent(existing?.localId ?: 0))
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun addStudent(studentCode: String, name: String, major: String) {
        dao.insertStudent(
            Student(
                studentCode = studentCode,
                name = name,
                major = major,
                syncStatus = "PENDING"
            )
        )
    }

    suspend fun syncPending(): Boolean {
        var allSuccessful = true
        dao.getPendingStudents().forEach { student ->
            try {
                val response = api.createStudent(
                    StudentCreateRequest(student.studentCode, student.name, student.major)
                )
                dao.updateStudent(
                    student.copy(
                        serverId = response.id,
                        syncStatus = "SYNCED",
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) {
                allSuccessful = false
                dao.updateStudent(
                    student.copy(
                        syncStatus = "FAILED",
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
        return allSuccessful
    }
}
