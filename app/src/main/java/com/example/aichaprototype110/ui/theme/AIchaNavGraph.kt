package com.example.aichaprototype110.ui.theme

import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aichaprototype110.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AIchaNavGraph(
    navController: NavHostController,
    startDestination: String,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInVertically(
                initialOffsetY = { it / 2 }, // masuk dari bawah setengah layar
            ) + fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            slideOutVertically(
                targetOffsetY = { -it / 2 }, // keluar ke atas setengah layar
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInVertically(
                initialOffsetY = { -it / 2 }, // balik dari atas
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutVertically(
                targetOffsetY = { it / 2 }, // keluar ke bawah
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable("login") {
            LoginScreen(navController = navController, isDarkTheme = isDarkTheme)
        }
        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("home") {
            MainScreen(navController = navController)
        }
        composable("setting") {
            SettingScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onLogout = onLogout
            )
        }
        composable("feedback") {
            FeedbackScreen()
        }
    }
}


