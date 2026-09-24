package com.example.studentmanagement.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.studentmanagement.model.Student

class StudentViewModel : ViewModel() {
    var students by mutableStateOf(
        listOf<Student>()
    )
        private set
    fun loadStudents() {
        students = listOf(

            Student(
                1,
                "66050160",
                "Techasit",

                "Computer Science"

            ),
            Student(
                2,
                "65010002",
                "Suda",

                "Information Technology"
            ),
            Student(
                3,

                "65010003",
                "Mana",
                "Digital Technology"
            )
        )
    }
}