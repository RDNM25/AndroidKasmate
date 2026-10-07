package com.arunk.dashboardlogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.arunk.dashboardlogin.ui.theme.DashboardLoginTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DashboardLoginTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.ime)),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        // Halaman 1: Login
                        composable("login") {
                            Loginscreen(
                                onLoginSuccess = {
                                    navController.navigate("kas")
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // Halaman 1b: Register
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.popBackStack()
                                },
                                onBackToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Halaman 2: Uang Kas + Data Siswa (home setelah login)
                        composable("kas") {
                            KasScreen(
                                onProfileClick = {
                                    navController.navigate("dashboard")
                                },
                                onHistoryClick = {
                                    navController.navigate("history")
                                },
                                onWithdrawClick = {
                                    navController.navigate("withdraw")
                                },
                                onAddStudentClick = {
                                    navController.navigate("add_student")
                                },
                                onStudentClick = { id ->
                                    navController.navigate("student_detail/$id")
                                }
                            )
                        }

                        // Halaman 2b: Tarik Kas
                        composable("withdraw") {
                            WithdrawScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Halaman 2c: Tambah Siswa
                        composable("add_student") {
                            AddStudentScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Halaman 3: Dashboard
                        composable("dashboard") {
                            DashboardScreen(
                                onBack = {
                                    navController.popBackStack()
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("kas") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        // Halaman 4: Riwayat Transaksi
                        composable("history") {
                            HistoryScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Halaman 5: Detail Siswa
                        composable(
                            route = "student_detail/{studentId}",
                            arguments = listOf(
                                navArgument("studentId") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val studentId = backStackEntry.arguments?.getLong("studentId") ?: -1L
                            StudentDetailScreen(
                                studentId = studentId,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
