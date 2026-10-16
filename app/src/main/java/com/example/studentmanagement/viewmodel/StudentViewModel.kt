package com.example.studentmanagement.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanagement.data.repository.StudentRepository
import com.example.studentmanagement.model.Student
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    var students by mutableStateOf(listOf<Student>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isOnline by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedStudent by mutableStateOf<Student?>(null)
        private set

    var showAddDialog by mutableStateOf(false)
        private set

    var showEditDialog by mutableStateOf(false)
        private set

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val (resultList, onlineState) = repository.getStudents()
                students = resultList
                isOnline = onlineState
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถโหลดข้อมูลได้"
            } finally {
                isLoading = false
            }
        }
    }

    fun addStudent(studentCode: String, name: String, major: String) {
        viewModelScope.launch {
            try {
                val newId = (students.maxOfOrNull { it.id } ?: 0) + 1
                val newStudent = Student(
                    id = newId,
                    studentCode = studentCode,
                    name = name,
                    major = major
                )
                repository.addStudent(newStudent)
                loadStudents()
                showAddDialog = false
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถเพิ่มข้อมูลได้"
            }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            try {
                repository.updateStudent(student)
                loadStudents()
                selectedStudent = student
                showEditDialog = false
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถแก้ไขข้อมูลได้"
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            try {
                repository.deleteStudent(student)
                if (selectedStudent?.id == student.id) {
                    selectedStudent = null
                }
                loadStudents()
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถลบข้อมูลได้"
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun selectStudent(student: Student?) {
        selectedStudent = student
    }

    fun openAddDialog() {
        showAddDialog = true
    }

    fun closeAddDialog() {
        showAddDialog = false
    }

    fun openEditDialog() {
        showEditDialog = true
    }

    fun closeEditDialog() {
        showEditDialog = false
    }

    fun filteredStudents(): List<Student> {
        if (searchQuery.isBlank()) return students
        return students.filter {
            it.studentCode.contains(searchQuery, ignoreCase = true) ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.major.contains(searchQuery, ignoreCase = true)
        }
    }
}
