package ru.gaket.themoviedb.presentation.review.whatliked

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.presentation.review.common.ReviewTextView

@Preview
@Composable
private fun WhatLikeViewPreview() {
    WhatLikeView(
        submittedReviewText = "Some review",
        isReviewTextEmpty = false,
        onTextChange = {},
        onSubmit = {}
    )
}

@Composable
internal fun WhatLikeView(viewModel: WhatLikeViewModel) {
    val submittedReviewState by viewModel.reviewTextState.collectAsStateWithLifecycle()

    WhatLikeView(
        submittedReviewText = submittedReviewState.submittedText,
        isReviewTextEmpty = submittedReviewState.isEmptyText,
        onTextChange = viewModel::onReviewTextChange,
        onSubmit = viewModel::submitText,
    )
}

@Composable
private fun WhatLikeView(
    submittedReviewText: String,
    isReviewTextEmpty: Boolean,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    ReviewTextView(
        title = stringResource(id = R.string.review_what_did_you_like),
        submittedReviewText = submittedReviewText,
        onTextChange = onTextChange,
        isReviewTextEmpty = isReviewTextEmpty,
        onSubmit = onSubmit,
    )
}