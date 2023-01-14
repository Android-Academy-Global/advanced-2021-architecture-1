package ru.gaket.themoviedb.presentation.review.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun RatingViewPreview() {
    RatingView(rating = 3)
}

@Composable
internal fun RatingView(
    modifier: Modifier = Modifier,
    rating: Int,
    maxRating: Int = 5,
    onRatingChange: (newRating: Int) -> Unit = {},
) {
    Row(modifier = Modifier) {
        repeat(maxRating) {
            Icon(
                imageVector = if (it < rating) {
                    Icons.Filled.StarRate
                } else {
                    Icons.Filled.StarBorder
                },
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.clickable(onClick = { onRatingChange(it + 1) }),
            )
        }
    }
}
