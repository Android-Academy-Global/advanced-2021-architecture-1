package ru.gaket.themoviedb.core.navigation


import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.navigation.animation.AnimatedNavHost
import ru.gaket.themoviedb.core.navigation.screens.authScreen
import ru.gaket.themoviedb.core.navigation.screens.mainScreen
import ru.gaket.themoviedb.core.navigation.screens.mainScreenRoute
import ru.gaket.themoviedb.core.navigation.screens.movieDetailsScreen
import ru.gaket.themoviedb.core.navigation.screens.navigateToAuth
import ru.gaket.themoviedb.core.navigation.screens.navigateToMovieDetails

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    webNavigator: WebNavigator
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = mainScreenRoute,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        }
    ) {
        mainScreen(
            onMovieClick = {
                navController.navigateToMovieDetails(movieId = it.id, title = it.title)
            }
        )

        authScreen(onBack = navController::popBackStack)

        movieDetailsScreen(
            onNavigateToAuthScreen = {
                navController.navigateToAuth()
            },
            onNavigateToReviewScreen = {
                // далее навигация не сделана
            },
            onBackClick = navController::popBackStack,
            onWebSearchClick = { movieId ->
                webNavigator.navigateTo(movieId)
            }
        )
    }
}