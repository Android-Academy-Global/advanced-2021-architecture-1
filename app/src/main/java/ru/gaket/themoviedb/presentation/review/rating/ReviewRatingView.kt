package ru.gaket.themoviedb.presentation.review.rating

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.presentation.review.common.RatingView

@Composable
@Preview
private fun ReviewRatingViewPreview() {
    ReviewRatingView(3, null, {}, {})
}

@Composable
internal fun ReviewRatingView(viewModel: RatingViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // TODO handle disabled state
    ReviewRatingView(
        rating = state.rating,
        error = state.error,
        onRatingChange = viewModel::onRatingChange,
        onSubmit = viewModel::submit,
    )
}

@Composable
private fun ReviewRatingView(
    rating: Int,
    @StringRes error: Int?,
    onRatingChange: (rating: Int) -> Unit,
    onSubmit: () -> Unit,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
) {
    if (error != null) {
        val message = stringResource(error)

        LaunchedEffect(snackbarState) {
            snackbarState.showSnackbar(
                message = message,
            )
        }
    }

    Column {
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = dimensionResource(id = R.dimen.space_normal)),
        ) {
            Text(
                text = stringResource(id = R.string.review_rating_message),
                fontSize = 24.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            RatingView(
                rating = rating,
                onRatingChange = onRatingChange,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = dimensionResource(id = R.dimen.space_normal)),
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.review_button_submit),
                )
            }
        }
        SnackbarHost(hostState = snackbarState)
    }
}
