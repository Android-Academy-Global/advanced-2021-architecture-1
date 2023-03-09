@file:OptIn(ExperimentalAnimationApi::class)

package ru.gaket.themoviedb.core.navigation.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
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