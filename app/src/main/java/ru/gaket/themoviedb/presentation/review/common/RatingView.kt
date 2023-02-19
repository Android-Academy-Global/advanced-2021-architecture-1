package ru.gaket.themoviedb.presentation.review.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
    Row(modifier = modifier) {
        repeat(maxRating) { index ->
            val icon = if (index < rating) {
                Icons.Filled.StarRate
            } else {
                Icons.Filled.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .clickable(onClick = { onRatingChange(index + 1) })
                    .testTag("star"),
            )
        }
    }
}

@Composable
internal fun RatingView(
    modifier: Modifier = Modifier,
    maxRating: Int = 5
) {
    var rating by remember { mutableStateOf(0) }
    Row(modifier = modifier) {
        repeat(maxRating) { index ->
            val icon = if (index < rating) {
                Icons.Filled.StarRate
            } else {
                Icons.Filled.StarBorder
            }
            println("RatingView ${icon.name}")
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .clickable(onClick = { rating = index + 1 })
                    .testTag("star")
            )
        }
    }
}

@Composable
fun LoginForm(onLogin: () -> Unit) {
    Column {
        var input by remember { mutableStateOf("") }
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Type your email") }
        )
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Login")
        }
    }
}


