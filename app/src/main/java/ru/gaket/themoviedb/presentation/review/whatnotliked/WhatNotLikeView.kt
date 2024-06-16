package ru.gaket.themoviedb.presentation.review.whatnotliked

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.gaket.themoviedb.R.string
import ru.gaket.themoviedb.presentation.review.common.ReviewTextView

@Preview
@Composable
private fun WhatNotLikeViewPreview() {
    WhatNotLikeView(
        submittedReviewText = "Some review",
        isReviewTextEmpty = false,
        onTextChange = {},
        onSubmit = {}
    )
}

@Composable
internal fun WhatNotLikeView(viewModel: WhatNotLikeViewModel) {
    val submittedReviewState by viewModel.reviewTextState.collectAsStateWithLifecycle()

    WhatNotLikeView(
        submittedReviewText = submittedReviewState.submittedText,
        isReviewTextEmpty = submittedReviewState.isEmptyText,
        onTextChange = viewModel::onReviewTextChange,
        onSubmit = viewModel::submitText,
    )
}

@Composable
private fun WhatNotLikeView(
    submittedReviewText: String,
    isReviewTextEmpty: Boolean,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    ReviewTextView(
        title = stringResource(id = string.review_what_did_not_like),
        submittedReviewText = submittedReviewText,
        onTextChange = onTextChange,
        isReviewTextEmpty = isReviewTextEmpty,
        onSubmit = onSubmit,
    )
}
