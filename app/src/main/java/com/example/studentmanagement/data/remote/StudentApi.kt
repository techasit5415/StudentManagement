package com.example.studentmanagement.data.remote

import com.example.studentmanagement.model.Student
import retrofit2.http.GET

interface StudentApi {
    @GET("users")
    suspend fun getStudents(): List<Student>
}
