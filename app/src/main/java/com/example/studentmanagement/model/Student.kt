package com.example.studentmanagement.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey
    val id: Int,
    val studentCode: String,
    val name: String,
    val major: String
)
