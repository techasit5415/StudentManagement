package com.example.studentmanagement.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studentmanagement.model.Student
import com.example.studentmanagement.viewmodel.StudentViewModel

@Composable
fun StudentScreen(modifier: Modifier = Modifier, viewModel: StudentViewModel) {
    val students by viewModel.students.collectAsState()
    val filteredStudents = if (viewModel.searchQuery.isBlank()) students else students.filter {
        it.studentCode.contains(viewModel.searchQuery, true) ||
            it.name.contains(viewModel.searchQuery, true) ||
            it.major.contains(viewModel.searchQuery, true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openAddDialog) { Text("+") }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Student Management", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = if (viewModel.isOnline) "Online" else "Offline",
                    color = if (viewModel.isOnline) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Search student") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = viewModel::syncStudents,
                enabled = !viewModel.isSyncing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isSyncing) CircularProgressIndicator()
                else Text("Sync Now")
            }
            viewModel.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredStudents, key = { it.localId }) { student ->
                    StudentCard(student)
                }
            }
        }
    }

    if (viewModel.showAddDialog) {
        AddStudentDialog(
            onDismiss = viewModel::closeAddDialog,
            onSave = viewModel::addStudent
        )
    }
}

@Composable
private fun StudentCard(student: Student) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${student.studentCode} - ${student.name}", style = MaterialTheme.typography.titleMedium)
            Text(student.major)
            Text("Sync: ${student.syncStatus}", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun AddStudentDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var studentCode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Student") },
        text = {
            Column {
                OutlinedTextField(studentCode, { studentCode = it }, label = { Text("Student Code") })
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                OutlinedTextField(major, { major = it }, label = { Text("Major") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(studentCode, name, major) },
                enabled = studentCode.isNotBlank() && name.isNotBlank() && major.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
