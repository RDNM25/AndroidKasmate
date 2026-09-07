package com.arunk.dashboardlogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                        .safeDrawingPadding(),
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

                        // Halaman 2: Uang Kas (home setelah login)
                        composable("kas") {
                            KasScreen(
                                onProfileClick = {
                                    navController.navigate("dashboard")
                                },
                                onHistoryClick = {
                                    navController.navigate("history")
                                }
                            )
                        }

                        // Halaman 3: Dashboard (dibuka lewat ikon profil)
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
                    }
                }
            }
        }
    }
}
