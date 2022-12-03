package ru.gaket.themoviedb.presentation.moviedetails.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.core.navigation.WebNavigator
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview.Add
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview.Existing
import ru.gaket.themoviedb.presentation.moviedetails.model.getCalendarYear
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsState.Result
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel

@Preview
@Composable
private fun MovieDetailsViewPreview() {
    MovieDetailsView(
        moviePosterUrl = "",
        movieTitle = "Spider-Man",
        movieYear = "1992",
        movieGenres = "Action, Comedy",
        movieRating = 5,
        movieOverview = "Lorem ipsum dolor mit amet",
        movieReviews = emptyList(),
        onReviewClick = {},
        onBackClick = {},
        onWebSearchClick = {},
    )
}

@Composable
internal fun MovieDetailsView(
    viewModel: MovieDetailsViewModel,
    navigator: Navigator,
    webNavigator: WebNavigator,
) {
    val state by viewModel.state.observeAsState()
    val moviePosterUrl = (state as? Result)?.movie?.thumbnail.orEmpty()
    val movieTitle = (state as? Result)?.movie?.title.orEmpty()
    val movieYear = (state as? Result)?.movie?.releaseDate?.getCalendarYear()?.toString().orEmpty()
    val movieGenres = (state as? Result)?.movie?.genres.orEmpty()
    val movieRating = (state as? Result)?.movie?.rating ?: 0
    val movieOverview = (state as? Result)?.movie?.overview.orEmpty()
    val movieReviews = (state as? Result)?.allReviews.orEmpty()

    MovieDetailsView(moviePosterUrl = moviePosterUrl,
        movieTitle = movieTitle,
        movieYear = movieYear,
        movieGenres = movieGenres,
        movieRating = movieRating,
        movieOverview = movieOverview,
        movieReviews = movieReviews,
        onReviewClick = viewModel::onReviewClick,
        onBackClick = navigator::back,
        onWebSearchClick = {
            webNavigator.navigateTo(viewModel.movieId)
        })
}

@Composable
private fun MovieDetailsView(
    moviePosterUrl: String,
    movieTitle: String,
    movieYear: String,
    movieGenres: String,
    movieRating: Int,
    movieOverview: String,
    movieReviews: List<MovieDetailsReview>,
    onReviewClick: (item: MovieDetailsReview) -> Unit,
    onBackClick: () -> Unit,
    onWebSearchClick: () -> Unit,
) {
    Column(
        verticalArrangement = spacedBy(dimensionResource(id = R.dimen.space_normal)),
        modifier = Modifier
            .background(colorResource(id = R.color.colorBackground))
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        MovieDetailsTopAppBar(
            onBackClick = onBackClick,
            onWebSearchClick = onWebSearchClick,
        )
        MovieDetails(
            posterUrl = moviePosterUrl,
            title = movieTitle,
            year = movieYear,
            genres = movieGenres,
            rating = movieRating,
        )
        Text(
            text = movieOverview,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.space_normal)),
        )
        LazyRow(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.space_normal))) {
            items(movieReviews) {
                when (it) {
                    is Add -> AddReviewItem(item = it, onClick = onReviewClick)
                    is Existing -> ExistingReviewItem(item = it, onClick = onReviewClick)
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsTopAppBar(
    onBackClick: () -> Unit,
    onWebSearchClick: () -> Unit,
) {
    TopAppBar(
        backgroundColor = Color.Transparent,
        contentColor = Color.Black,
        elevation = 0.dp,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(id = R.string.review_button_back),
            )
        }
        Spacer(modifier = Modifier.weight(weight = 1f))
        IconButton(onClick = onWebSearchClick) {
            Icon(painter = painterResource(id = R.drawable.ic_search_web),
                contentDescription = stringResource(id = R.string.review_button_search_web))
        }
    }
}

@Composable
private fun MovieDetails(
    posterUrl: String,
    title: String,
    year: String,
    genres: String,
    rating: Int,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = dimensionResource(id = R.dimen.space_normal))) {
        AsyncImage(
            model = posterUrl,
            contentDescription = stringResource(id = R.string.content_cinema_poster),
            placeholder = painterResource(id = R.drawable.ic_cinema_grey_100),
            error = painterResource(id = R.drawable.ic_cinema_grey_100),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(dimensionResource(id = R.dimen.posterHeight))
                .aspectRatio(ratio = 2f / 3f)
                .background(color = colorResource(id = R.color.posterBackground),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.cornerRad))),
        )
        Column(
            verticalArrangement = spacedBy(dimensionResource(id = R.dimen.space_small)),
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.space_normal),
                top = dimensionResource(id = R.dimen.space_small),
            ),
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(text = year, fontSize = 16.sp)
            Text(
                text = genres,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(id = R.string.movie_details_rating_pattern, rating),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun AddReviewItem(
    item: Add,
    onClick: (item: Add) -> Unit,
) {
    Card(
        elevation = dimensionResource(id = R.dimen.reviewElevation),
        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.cornerRad)),
        backgroundColor = colorResource(id = R.color.colorReviewBackground),
        modifier = Modifier
            .size(width = 200.dp, height = 300.dp)
            .padding(
                horizontal = dimensionResource(id = R.dimen.space_small),
                vertical = dimensionResource(id = R.dimen.space_medium),
            )
            .clickable(onClick = { onClick(item) }),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Center,
            modifier = Modifier.padding(all = dimensionResource(id = R.dimen.space_medium)),
        ) {
            Icon(imageVector = Filled.Add, contentDescription = null) // TODO Move text to presentation layer
            Text(
                text = if (item.isAuthorized) {
                    stringResource(id = R.string.review_add_label)
                } else {
                    stringResource(id = R.string.authiorize_to_add_review_label)
                },
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ExistingReviewItem(
    item: Existing,
    onClick: (item: Existing) -> Unit,
) {
    Card(
        elevation = dimensionResource(id = R.dimen.reviewElevation),
        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.cornerRad)),
        backgroundColor = colorResource(id = R.color.colorReviewBackground),
        modifier = Modifier
            .size(width = 200.dp, height = 300.dp)
            .padding(
                horizontal = dimensionResource(id = R.dimen.space_small),
                vertical = dimensionResource(id = R.dimen.space_medium),
            )
            .clickable(onClick = { onClick(item) }),
    ) {
        Text(
            // TODO Move to presentation layer
            text = when (item) {
                is Existing.My -> stringResource(id = R.string.review_my_review)
                is Existing.Someone -> item.info.author.value
            },
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = item.review.liked,
            overflow = TextOverflow.Ellipsis,
            maxLines = 4,
            fontSize = 14.sp,
            color = colorResource(id = R.color.colorReviewLiked),
        )
        Text(
            text = item.review.liked,
            overflow = TextOverflow.Ellipsis,
            maxLines = 4,
            fontSize = 14.sp,
            color = colorResource(id = R.color.colorReviewDisliked),
        )
    }
}
