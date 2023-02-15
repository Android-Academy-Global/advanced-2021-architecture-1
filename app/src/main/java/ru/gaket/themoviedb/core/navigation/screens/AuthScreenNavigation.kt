package ru.gaket.themoviedb.core.navigation.screens

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ru.gaket.themoviedb.presentation.auth.view.AuthView

private const val authScreenRoute = "auth"

fun NavGraphBuilder.authScreen(onBack: () -> Unit) {
    composable(authScreenRoute) {
        AuthView(
            onAuthorized = onBack
        )
    }
}

fun NavHostController.navigateToAuth() {
    navigate(authScreenRoute)
}