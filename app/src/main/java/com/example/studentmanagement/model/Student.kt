package com.example.studentmanagement.model
data class Student(
    val id: Int,
    val studentCode: String,
    val name: String,
    val major: String
)
val students = listOf(
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