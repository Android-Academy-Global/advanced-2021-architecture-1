package ru.gaket.themoviedb.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ru.gaket.themoviedb.R

@Composable
@Preview
private fun ReviewViewPreview() {
    ReviewView("", "", null, null)
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
internal fun ReviewView(viewModel: ReviewViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val movie = state.movie
    val createReviewForm = state.createState?.form
    if (movie != null) {
        ReviewView(
            thumbnailUrl = movie.thumbnail,
            title = stringResource(id = R.string.review_rate_placeholder, movie.title),
            whatLiked = if (createReviewForm?.whatLiked != null) {
                stringResource(id = R.string.review_liked_placeholder, createReviewForm.whatLiked)
            } else {
                null
            },
            whatNotLiked = if (createReviewForm?.whatDidNotLike != null) {
                stringResource(id = R.string.review_liked_placeholder, createReviewForm.whatDidNotLike)
            } else {
                null
            },
        )
    }
}

@Composable
private fun ReviewView(
    thumbnailUrl: String,
    title: String,
    whatLiked: String?,
    whatNotLiked: String?,
) {
    Card(
        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.space_medium)),
        elevation = dimensionResource(id = R.dimen.space_medium),
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = dimensionResource(id = R.dimen.space_medium))
    ) {
        Row {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = stringResource(id = R.string.content_cinema_poster),
                placeholder = painterResource(id = R.drawable.ic_cinema_grey_100),
                error = painterResource(id = R.drawable.ic_cinema_grey_100),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.posterWidth))
                    .aspectRatio(ratio = 2f / 3f)
                    .background(color = colorResource(id = R.color.posterBackground)),
            )
            Column(modifier = Modifier.padding(all = dimensionResource(id = R.dimen.space_normal))) {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                if (whatLiked != null) {
                    Text(
                        text = whatLiked,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                    )
                }
                if (whatNotLiked != null) {
                    Text(
                        text = whatNotLiked,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}