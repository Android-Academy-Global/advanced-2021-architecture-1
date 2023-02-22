@file:OptIn(ExperimentalAnimationApi::class)

package ru.gaket.themoviedb.presentation.moviedetails.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.core.navigation.WebNavigator
import ru.gaket.themoviedb.domain.review.models.Review
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview.Add
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview.Existing
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import ru.gaket.themoviedb.presentation.review.common.RatingView


private const val CUSTOM_ANIMATION_DURATION = 1500

@Preview
@Composable
private fun MovieDetailsViewPreview() {
    MovieDetailsView(
        isLiked = true,
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
    viewModel: MovieDetailsViewModel,
    navigator: Navigator,
    webNavigator: WebNavigator,
) {
    val state by viewModel.movieDetailsState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        MovieDetailsView(
            showLoading = state.isMovieDetailsLoading,
            moviePosterUrl = state.moviePosterUrl,
            movieTitle = state.movieTitle,
            movieYear = state.movieYear,
            movieGenres = state.movieGenres,
            movieRating = state.movieRating,
            movieOverview = state.movieOverview,
            movieReviews = state.movieReviews,
            isLiked = state.liked,
            onAddReviewClick = { navigator.navigateTo(state.screenToNavigate) },
            onBackClick = navigator::back,
            onWebSearchClick = {
                webNavigator.navigateTo(viewModel.movieId)
            },
            onLikeClick = { viewModel.startAnimation() }
        )

        LikeAnimationCompose(
            modifier = Modifier.fillMaxSize(),
            isActive = state.animationIsActive,
            animationFinished = {
                viewModel.stopAnimation()
                viewModel.toggleLikeState()
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
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
    isLiked: Boolean,
    onAddReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    onWebSearchClick: () -> Unit,
    onLikeClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = spacedBy(dimensionResource(id = R.dimen.space_normal)),
        modifier = Modifier
            .background(colorResource(id = R.color.colorBackground))
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        MovieDetailsTopAppBar(
            isLiked = isLiked,
            onBackClick = onBackClick,
            onWebSearchClick = onWebSearchClick,
            onLikeClick = onLikeClick
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
            val isVisible = remember {
                MutableTransitionState(false).also {
                    it.targetState = true
                }
            }
            val duration = CUSTOM_ANIMATION_DURATION
            AnimatedVisibility(
                visibleState = isVisible,
                enter = EnterTransition.None
            ) {
                Column(
                    verticalArrangement = spacedBy(dimensionResource(id = R.dimen.space_normal)),
                ) {
                    MovieDetails(
                        modifier = Modifier.animateEnterExit(
                            enter = fadeIn(tween(duration)) + slideInVertically(tween(duration)),
                        ),
                        posterUrl = moviePosterUrl,
                        title = movieTitle,
                        year = movieYear,
                        genres = movieGenres,
                        rating = movieRating,
                    )
                    MovieDescription(
                        modifier = Modifier.animateEnterExit(
                            enter = fadeIn(tween(duration)) + slideInHorizontally(tween(duration))
                        ),
                        movieOverview = movieOverview
                    )
                    MovieReviews(
                        modifier = Modifier.animateEnterExit(
                            enter = fadeIn(tween(duration)) + slideInVertically(tween(duration)) { it / 2 }
                        ),
                        movieReviews = movieReviews,
                        onAddReviewClick = onAddReviewClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieReviews(
    modifier: Modifier = Modifier,
    movieReviews: List<MovieDetailsReview>,
    onAddReviewClick: () -> Unit
) {
    LazyRow(modifier = modifier.padding(vertical = dimensionResource(id = R.dimen.space_normal))) {
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

@Composable
private fun MovieDescription(
    modifier: Modifier = Modifier,
    movieOverview: String
) {
    Text(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.space_normal)),
        text = movieOverview,
        fontSize = 20.sp,
    )
}

@Composable
private fun MovieDetailsTopAppBar(
    isLiked: Boolean,
    onBackClick: () -> Unit,
    onWebSearchClick: () -> Unit,
    onLikeClick: () -> Unit = {}
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
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = if (isLiked) {
                    Color.Red
                } else {
                    Color.LightGray
                }
            )
        }
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
    modifier: Modifier = Modifier,
    posterUrl: String,
    title: String,
    year: String,
    genres: String,
    rating: Int,
) {
    var isToggled by remember { mutableStateOf(false) }

    val duration = CUSTOM_ANIMATION_DURATION
    val transition = updateTransition(targetState = isToggled)

    // Height DP value will be animated and interpolated with tween() spec
    val height by transition.animateDp(
        label = "Width Animation",
        transitionSpec = { tween(duration) }
    ) { toggled ->
        if (toggled) LocalConfiguration.current.screenWidthDp.dp else 100.dp
    }

    // Alpha Float value will be animated and interpolated with tween() spec
    val alpha by transition.animateFloat(
        label = "Alpha Animation",
        transitionSpec = { tween(duration) }
    ) { toggled ->
        if (toggled) 0f else 1f
    }

    Column(modifier = modifier.fillMaxWidth()) {
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
                    .width(height)
                    .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.cornerRad)))
                    .aspectRatio(ratio = 2f / 3f)
                    .clickable { isToggled = !isToggled }
                    .background(
                        color = colorResource(id = R.color.posterBackground),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.cornerRad))
                    ),
            )
            FilmInfoSection(
                modifier = Modifier.alpha(alpha),
                title = title,
                year = year,
                genres = genres,
                rating = rating
            )
        }

        transition.AnimatedVisibility(
            visible = { it },
            enter = fadeIn(tween(duration)),
            exit = fadeOut(tween(duration))
        ) {
            FilmInfoSection(
                title = title,
                year = year,
                genres = genres,
                rating = rating
            )
        }
    }
}

/**
 * Encapsulates Film Info section.
 * Used in several places.
 */
@Composable
private fun FilmInfoSection(
    modifier: Modifier = Modifier,
    title: String,
    year: String,
    genres: String,
    rating: Int
) {
    Column(
        verticalArrangement = spacedBy(dimensionResource(id = R.dimen.space_small)),
        modifier = modifier
            .padding(
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
