package ru.gaket.themoviedb.presentation.review.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.gaket.themoviedb.R

@Preview
@Composable
private fun RatingViewPreview() {
    RatingView(rating = 3)
}

@Preview
@Composable
private fun StatefulRatingViewPreview() {
    RatingViewStateful()
}

@Composable
internal fun RatingView(
    modifier: Modifier = Modifier,
    rating: Int,
    maxRating: Int = 5,
    onRatingChange: (newRating: Int) -> Unit = {},
) {
    Row(modifier = Modifier) {
        repeat(maxRating) { starIndex ->
            Icon(
                imageVector = if (starIndex < rating) {
                    Icons.Filled.StarRate
                } else {
                    Icons.Filled.StarBorder
                },
                contentDescription = stringResource(id = R.string.stars_description),
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.clickable(onClick = { onRatingChange(starIndex + 1) }),
            )
        }
    }
}


@Composable
internal fun RatingViewStateful(
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    onRatingChange: (newRating: Int) -> Unit = {},
) {

    var rating by remember {
        mutableStateOf(0)
    }

    Row(modifier = Modifier) {
        repeat(maxRating) { starIndex ->
            Icon(
                imageVector = if (starIndex < rating) {
                    Icons.Filled.StarRate
                } else {
                    Icons.Filled.StarBorder
                },
                contentDescription = stringResource(id = R.string.stars_description),
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.clickable(onClick = {
                    rating = starIndex + 1
                    onRatingChange(rating)
                }),
            )
        }
    }
}
