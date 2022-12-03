package ru.gaket.themoviedb.presentation.movies.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.MovieDetailsScreen
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.domain.movies.models.SearchMovie
import ru.gaket.themoviedb.domain.movies.models.SearchMovieWithMyReview
import ru.gaket.themoviedb.presentation.movies.viewmodel.MoviesResult
import ru.gaket.themoviedb.presentation.movies.viewmodel.MoviesViewModel

@Preview(showSystemUi = true)
@Composable
private fun MoviesViewPreview() {
    MoviesView(
        foundMovies = emptyList(),
        isSearchInProgress = false,
        onNewQuery = {},
        onMovieClick = {},
    )
}

@Composable
internal fun MoviesView(viewModel: MoviesViewModel, navigator: Navigator) {
    val result by viewModel.searchResult.observeAsState()
    MoviesView(
        foundMovies = (result as? MoviesResult.SuccessResult)?.result ?: emptyList(),
        isSearchInProgress = result is MoviesResult.Loading,
        onNewQuery = viewModel::onNewQuery,
        onMovieClick = { movie ->
            navigator.navigateTo(MovieDetailsScreen(movie.id, movie.title))
        }
    )
}

@Composable
private fun MoviesView(
    foundMovies: List<SearchMovieWithMyReview>,
    isSearchInProgress: Boolean,
    onNewQuery: (String) -> Unit,
    onMovieClick: (movie: SearchMovie) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        var input by remember { mutableStateOf("") }
        SearchView(
            input = input,
            hint = stringResource(id = R.string.hint_search_query),
            onInputChanged = {
                input = it
                onNewQuery(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
        ) {
            SearchProgressView(isInProgress = isSearchInProgress)
        }
        if (foundMovies.isEmpty()) {
            Text(
                text = stringResource(id = R.string.movies_placeholder),
                color = colorResource(id = R.color.textColorPrimary),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
            )
        } else {
            // TODO Hide keyboard on scroll
            LazyVerticalGrid(
                columns = Fixed(count = integerResource(id = R.integer.search_results_span_count)),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(foundMovies) { movie ->
                    MovieView(
                        movie = movie,
                        onClick = { searchMovie ->
                            onMovieClick(searchMovie)
                        },
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchView(
    modifier: Modifier = Modifier,
    input: String,
    inputTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 14.sp,
    ),
    hint: String,
    hintTextStyle: TextStyle = TextStyle(
        color = colorResource(id = R.color.textColorSecondary),
        fontSize = 14.sp,
    ),
    onInputChanged: (String) -> Unit,
    trailingView: @Composable () -> Unit,
) {
    BasicTextField(
        value = input,
        singleLine = true,
        textStyle = inputTextStyle,
        onValueChange = onInputChanged,
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
    ) { innerTextField ->
        Surface(
            shape = RoundedCornerShape(size = 12.dp),
            elevation = 8.dp,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Box {
                    if (input.isEmpty()) {
                        Text(text = hint, style = hintTextStyle)
                    }
                    innerTextField()
                }
                trailingView()
            }
        }
    }
}

@Composable
private fun SearchProgressView(isInProgress: Boolean) {
    if (isInProgress) {
        CircularProgressIndicator(modifier = Modifier.size(22.dp))
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_search_grey_16),
            contentDescription = stringResource(id = R.string.search_icon),
        )
    }
}

@Composable
private fun MovieView(
    movie: SearchMovieWithMyReview,
    onClick: (searchMovie: SearchMovie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.cornerRad)))
            .clickable(onClick = { onClick(movie.movie) })
    ) {
        AsyncImage(
            model = movie.movie.thumbnail,
            contentDescription = stringResource(id = R.string.content_cinema_poster),
            placeholder = painterResource(id = R.drawable.ic_cinema_grey_100),
            error = painterResource(id = R.drawable.ic_cinema_grey_100),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(ratio = 2f / 3f)
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.posterBackground)),
        )
        Text(
            text = movie.movie.title.uppercase(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.colorTitles),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = verticalGradient(
                        colors = listOf(
                            Color(0x20000000),
                            Color(0x00000000),
                        ),
                    ),
                )
                .padding(
                    horizontal = dimensionResource(id = R.dimen.space_small),
                    vertical = dimensionResource(id = R.dimen.space_medium),
                ),
        )
    }
}