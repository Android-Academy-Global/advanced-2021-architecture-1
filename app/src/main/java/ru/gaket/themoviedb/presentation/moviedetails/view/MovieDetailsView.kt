package ru.gaket.themoviedb.presentation.moviedetails.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.core.navigation.WebNavigator
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel

@Preview
@Composable
private fun MovieDetailsViewPreview() {
    MovieDetailsView()
}

@Composable
internal fun MovieDetailsView(
    viewModel: MovieDetailsViewModel,
    navigator: Navigator,
    webNavigator: WebNavigator,
) {
    MovieDetailsView()
}

@Composable
private fun MovieDetailsView() {
}