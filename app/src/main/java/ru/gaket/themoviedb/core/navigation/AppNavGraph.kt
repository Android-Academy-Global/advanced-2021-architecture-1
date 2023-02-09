package ru.gaket.themoviedb.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.gaket.themoviedb.presentation.moviedetails.view.ComposeMovieDetailsFragment
import ru.gaket.themoviedb.presentation.moviedetails.view.MovieDetailsView
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import ru.gaket.themoviedb.presentation.movies.view.MoviesView

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    webNavigator: WebNavigator
) {
    val mainScreen = MoviesScreen()

    NavHost(
        navController = navController,
        startDestination = mainScreen.route,
        modifier = modifier
    ) {
        composable(mainScreen.route) {
            MoviesView(onMovieClick = {
                navController.navigate(
                    MovieDetailsScreen(it.id, it.title).route
                )
            }
            )
        }

        composable(MovieDetailsScreen.registrationRoute) { backStack ->
            val movieId = backStack.arguments?.getString(MovieDetailsScreen.movieIdKey)
            val title = backStack.arguments?.getString(MovieDetailsScreen.titleKey)

            /**
             * Так не работает, тк в [MovieDetailsViewModel] @AssistedInject.
             * а подсунуть свою Factory по аналогии с [ComposeMovieDetailsFragment] не получится, тк
             * hiltViewModel под капотом использует свою internal factory.
             *
             * Механизмов для инъекции @Assisted параметров не нашел
             */
            val viewModel = hiltViewModel<MovieDetailsViewModel>()

            MovieDetailsView(
                viewModel = viewModel,
                onNavigateIntent = { screenToNavigate ->
                    navController.navigate(screenToNavigate.route)
                },
                onBackClick = navController::popBackStack,
                onWebSearchClick = {
                    webNavigator.navigateTo(1L) //viewModel.movieId)
                }
            )
        }
    }
}