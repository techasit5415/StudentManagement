package com.example.studentmanagement.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanagement.data.repository.StudentRepository
import com.example.studentmanagement.model.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {
    val students: StateFlow<List<Student>> = repository.observeStudents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    var isOnline by mutableStateOf(false)
        private set
    var isSyncing by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var searchQuery by mutableStateOf("")
        private set
    var showAddDialog by mutableStateOf(false)
        private set

    init {
        refreshFromServer()
    }

    fun refreshFromServer() {
        viewModelScope.launch {
            isOnline = repository.refreshFromServer()
        }
    }

    fun addStudent(studentCode: String, name: String, major: String) {
        viewModelScope.launch {
            repository.addStudent(studentCode, name, major)
            showAddDialog = false
        }
    }

    fun syncStudents() {
        viewModelScope.launch {
            isSyncing = true
            errorMessage = null
            try {
                isOnline = repository.syncPending()
                if (!isOnline) errorMessage = "Sync ไม่สำเร็จ ข้อมูลยังอยู่ใน Room"
            } catch (_: Exception) {
                isOnline = false
                errorMessage = "ไม่สามารถ Sync ข้อมูลได้"
            } finally {
                isSyncing = false
            }
        }
    }

    fun onSearchQueryChange(query: String) { searchQuery = query }
    fun openAddDialog() { showAddDialog = true }
    fun closeAddDialog() { showAddDialog = false }

    fun filteredStudents(): List<Student> {
        if (searchQuery.isBlank()) return students.value
        return students.value.filter {
            it.studentCode.contains(searchQuery, ignoreCase = true) ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.major.contains(searchQuery, ignoreCase = true)
        }
    }
}
