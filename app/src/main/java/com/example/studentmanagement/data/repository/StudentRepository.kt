package com.example.studentmanagement.data.repository

import com.example.studentmanagement.model.Student

class StudentRepository {
    fun getStudents(): List<Student> {
        return listOf(
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
