package ru.gaket.themoviedb.presentation.review.common

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gaket.themoviedb.R

@Preview
@Composable
private fun ReviewTextViewPreview() {
    ReviewTextView(
        title = "Hello World",
        submittedReviewText = "Some review",
        isReviewTextEmpty = false,
        onTextChange = {},
        onSubmit = {},
    )
}

@Composable
internal fun ReviewTextView(
    title: String,
    submittedReviewText: String,
    isReviewTextEmpty: Boolean,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {

    if (isReviewTextEmpty) ShowToastOnEmptyText()

    Column(
        verticalArrangement = spacedBy(dimensionResource(id = R.dimen.space_normal)),
        modifier = Modifier
            .fillMaxSize()
            .padding(all = dimensionResource(id = R.dimen.space_normal)),
    ) {
        Text(
            text = title,
            maxLines = 1,
            fontSize = 24.sp,
            overflow = TextOverflow.Ellipsis,
        )
        BasicTextField(
            value = submittedReviewText,
            onValueChange = onTextChange,
            decorationBox = { innerTextField ->
                Surface(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(size = 12.dp),
                ) {
                    Box(
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen.space_normal),
                            vertical = dimensionResource(id = R.dimen.space_medium),
                        )
                    ) {
                        innerTextField()
                    }
                }
            },
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
        )
        Button(
            onClick = { onSubmit() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(id = R.string.review_button_next))
        }
    }
}

@Composable
private fun ShowToastOnEmptyText(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    val message = stringResource(id = R.string.review_error_should_not_be_empty)

    LaunchedEffect(scaffoldState.snackbarHostState) {
        scaffoldState.snackbarHostState.showSnackbar(
            message = message,
        )
    }
}