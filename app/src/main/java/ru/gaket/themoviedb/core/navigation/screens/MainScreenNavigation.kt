package ru.gaket.themoviedb.core.navigation.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import ru.gaket.themoviedb.domain.movies.models.SearchMovie
import ru.gaket.themoviedb.presentation.movies.view.MoviesView

const val mainScreenRoute = "main"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainScreen(onMovieClick: (movie: SearchMovie) -> Unit) {
    composable(mainScreenRoute) {
        MoviesView(
            onMovieClick = onMovieClick
        )
    }
}

fun NavHostController.navigateToMain() {
    navigate(mainScreenRoute)
}