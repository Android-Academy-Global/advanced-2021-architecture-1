package ru.gaket.themoviedb.presentation.moviedetails.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.Screen
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.review.models.Review
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview.Add
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview.Existing
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import ru.gaket.themoviedb.presentation.review.common.RatingView

@Preview
@Composable
private fun MovieDetailsViewPreview() {
    MovieDetailsView(
        showLoading = false,
        moviePosterUrl = "",
        movieTitle = "Spider-Man",
        movieYear = "1992",
        movieGenres = "Action, Comedy",
        movieRating = 5,
        movieOverview = "Lorem ipsum dolor mit amet",
        movieReviews = emptyList(),
        onAddReviewClick = {},
        onBackClick = {},
        onWebSearchClick = {},
    )
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
internal fun MovieDetailsView(
    movieId: MovieId,
    loadingTitle: String,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    onNavigateIntent: (Screen) -> Unit,
    onBackClick: () -> Unit,
    onWebSearchClick: () -> Unit
) {
    val state by viewModel.movieDetailsState.collectAsStateWithLifecycle()

    MovieDetailsView(
        showLoading = state.isMovieDetailsLoading,
        moviePosterUrl = state.moviePosterUrl,
        movieTitle = state.movieTitle,
        movieYear = state.movieYear,
        movieGenres = state.movieGenres,
        movieRating = state.movieRating,
        movieOverview = state.movieOverview,
        movieReviews = state.movieReviews,
        onAddReviewClick = {
            state.screenToNavigate?.let {
                onNavigateIntent(it)
            }
        },
        onBackClick = onBackClick,
        onWebSearchClick = onWebSearchClick
    )

    LaunchedEffect(true) {
        viewModel.onStart(
            movieId = movieId,
            title = loadingTitle
        )
    }
}

@Composable
private fun MovieDetailsView(
    showLoading: Boolean,
    moviePosterUrl: String,
    movieTitle: String,
    movieYear: String,
    movieGenres: String,
    movieRating: Int,
    movieOverview: String,
    movieReviews: List<MovieDetailsReview>,
    onAddReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    onWebSearchClick: () -> Unit,
) {
    Column(
        verticalArrangement = spacedBy(dimensionResource(id = R.dimen.space_normal)),
        modifier = Modifier
            .background(colorResource(id = R.color.colorBackground))
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        MovieDetailsTopAppBar(
            onBackClick = onBackClick,
            onWebSearchClick = onWebSearchClick,
        )
        if (showLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
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
                items(movieReviews) { review ->
                    when (review) {
                        is Add -> AddReviewItem(item = review, onClick = onAddReviewClick)
                        is Existing.My -> ExistingReviewItem(
                            text = stringResource(id = review.textRes),
                            review = review.review,
                        )

                        is Existing.Someone -> ExistingReviewItem(
                            text = review.text,
                            review = review.review,
                        )
                    }
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
            Icon(
                painter = painterResource(id = R.drawable.ic_search_web),
                contentDescription = stringResource(id = R.string.review_button_search_web)
            )
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.space_normal))
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = stringResource(id = R.string.content_cinema_poster),
            placeholder = painterResource(id = R.drawable.ic_cinema_grey_100),
            error = painterResource(id = R.drawable.ic_cinema_grey_100),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(dimensionResource(id = R.dimen.posterHeight))
                .aspectRatio(ratio = 2f / 3f)
                .background(
                    color = colorResource(id = R.color.posterBackground),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.cornerRad))
                ),
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
    onClick: () -> Unit,
) {
    Card(
        elevation = dimensionResource(id = R.dimen.reviewElevation),
        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.cornerRad)),
        backgroundColor = colorResource(id = R.color.colorReviewBackground),
        modifier = Modifier
            .size(width = 200.dp, height = 300.dp)
            .padding(
                horizontal = dimensionResource(id = R.dimen.space_normal),
                vertical = dimensionResource(id = R.dimen.space_medium),
            )
            .clickable(onClick = { onClick() }),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Center,
            modifier = Modifier.padding(all = dimensionResource(id = R.dimen.space_medium)),
        ) {
            Icon(
                imageVector = Filled.Add,
                contentDescription = null,
            )
            Text(
                text = stringResource(item.textRes),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ExistingReviewItem(
    text: String,
    review: Review,
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
    ) {
        Column(
            verticalArrangement = SpaceBetween,
            modifier = Modifier.padding(all = dimensionResource(id = R.dimen.space_medium)),
        ) {
            Column(verticalArrangement = spacedBy(dimensionResource(id = R.dimen.space_normal))) {
                Text(
                    text = text,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                RatingView(rating = review.rating.starsCount)
            }
            Text(
                text = review.liked,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                fontSize = 14.sp,
                color = colorResource(id = R.color.colorReviewLiked),
            )
            Text(
                text = review.disliked,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                fontSize = 14.sp,
                color = colorResource(id = R.color.colorReviewDisliked),
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.space_small))
            )
        }
    }
}
