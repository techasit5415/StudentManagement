package com.example.studentmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.studentmanagement.data.local.AppDatabase
import com.example.studentmanagement.data.remote.RetrofitClient
import com.example.studentmanagement.data.remote.AuthApi
import com.example.studentmanagement.data.remote.StudentApi
import com.example.studentmanagement.data.remote.TokenStorage
import com.example.studentmanagement.data.repository.StudentRepository
import com.example.studentmanagement.ui.login.LoginScreen
import com.example.studentmanagement.ui.login.LoginViewModel
import com.example.studentmanagement.ui.theme.StudentManagementTheme
import com.example.studentmanagement.ui.theme.StudentScreen
import com.example.studentmanagement.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenStorage = TokenStorage(applicationContext)
        val authenticatedRetrofit = RetrofitClient.create(tokenStorage)
        val authApi = RetrofitClient.createUnauthenticated().create(AuthApi::class.java)
        val studentApi = authenticatedRetrofit.create(StudentApi::class.java)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "student_database"
        ).addMigrations(MIGRATION_1_2).build()
        val repository = StudentRepository(
            dao = db.studentDao(),
            api = studentApi
        )
        val viewModel = StudentViewModel(repository)
        val loginViewModel = LoginViewModel(authApi, tokenStorage)

        setContent {
            StudentManagementTheme {
                var isLoggedIn by remember { mutableStateOf(tokenStorage.getToken() != null) }
                if (isLoggedIn) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        StudentScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = viewModel
                        )
                    }
                } else {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
        }
    }

    private companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS students_new (" +
                        "localId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "serverId INTEGER, studentCode TEXT NOT NULL, name TEXT NOT NULL, " +
                        "major TEXT NOT NULL, syncStatus TEXT NOT NULL, updatedAt INTEGER NOT NULL)"
                )
                database.execSQL(
                    "INSERT INTO students_new (localId, serverId, studentCode, name, major, syncStatus, updatedAt) " +
                        "SELECT id, id, studentCode, name, major, 'SYNCED', 0 FROM students"
                )
                database.execSQL("DROP TABLE students")
                database.execSQL("ALTER TABLE students_new RENAME TO students")
            }
        }
    }
}
