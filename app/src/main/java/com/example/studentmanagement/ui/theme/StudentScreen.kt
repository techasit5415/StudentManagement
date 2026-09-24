package com.example.studentmanagement.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentmanagement.viewmodel.StudentViewModel

@Composable
fun StudentScreen(
    modifier: Modifier = Modifier,
    viewModel: StudentViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Student Management",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                viewModel.loadStudents()
            }
        ) {
            Text("Load Students")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn {
            items(viewModel.students, key = { it.id }) { student ->
                Text(
                    text = "${student.studentCode} - ${student.name}"
                )
                Text(
                    text = student.major
                )
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentScreenPreview() {
    StudentManagementTheme {
        StudentScreen()
    }
}
