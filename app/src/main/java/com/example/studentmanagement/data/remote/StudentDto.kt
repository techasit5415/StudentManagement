package com.example.studentmanagement.data.remote

import com.example.studentmanagement.model.Student
import com.google.gson.annotations.SerializedName

data class StudentDto(
    val id: Int,
    @SerializedName("student_code") val studentCode: String,
    val name: String,
    val major: String
) {
    fun toStudent(localId: Int = 0) = Student(
        localId = localId,
        serverId = id,
        studentCode = studentCode,
        name = name,
        major = major,
        syncStatus = "SYNCED"
    )
}

data class StudentCreateRequest(
    @SerializedName("student_code") val studentCode: String,
    val name: String,
    val major: String
)
