package com.example.ratemelocalai.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Consent : Screen("consent")
    object Home : Screen("home")
    object Scan : Screen("scan")
    object Results : Screen("results")
}
