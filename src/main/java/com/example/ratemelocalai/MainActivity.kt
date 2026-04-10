package com.example.ratemelocalai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ratemelocalai.navigation.Screen
import com.example.ratemelocalai.ui.screens.*
import com.example.ratemelocalai.ui.theme.RateMeLocalAITheme
import com.example.ratemelocalai.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RateMeLocalAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()

    if (isAnalyzing) {
        LoadingScreen("Processing Neural Analysis...")
    } else {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen {
                    val target = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route
                    navController.navigate(target) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onFinish = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Consent.route)
                })
            }
            composable(Screen.Consent.route) {
                ConsentScreen(onAccepted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onStartScan = { navController.navigate(Screen.Scan.route) },
                    onSettings = { navController.navigate("settings") }
                )
            }
            composable(Screen.Scan.route) {
                ScanScreen(
                    viewModel = viewModel,
                    onResult = { result ->
                        viewModel.setAnalysisResult(result)
                        navController.navigate(Screen.Results.route) {
                            popUpTo(Screen.Scan.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Results.route) {
                ResultsScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onSettings = { navController.navigate("settings") }
                )
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onRestartOnboarding = {
                        viewModel.resetOnboarding()
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
