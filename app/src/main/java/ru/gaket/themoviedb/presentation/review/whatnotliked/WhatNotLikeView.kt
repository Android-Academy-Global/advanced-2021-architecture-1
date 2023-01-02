package ru.gaket.themoviedb.presentation.review.whatnotliked

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.gaket.themoviedb.R.string
import ru.gaket.themoviedb.presentation.review.common.ReviewTextView

@Preview
@Composable
private fun WhatNotLikeViewPreview() {
    WhatNotLikeView(
        submittedReviewText = "Some review",
        onSubmit = {}
    )
}

@Composable
internal fun WhatNotLikeView(viewModel: WhatNotLikeViewModel) {
    val submittedReviewText by viewModel.initialValue.observeAsState("")
    WhatNotLikeView(
        submittedReviewText = submittedReviewText,
        onSubmit = viewModel::submitInfo,
    )
}

@Composable
private fun WhatNotLikeView(
    submittedReviewText: String,
    onSubmit: (reviewText: String) -> Unit,
) {
    ReviewTextView(
        title = stringResource(id = string.review_what_did_not_like),
        submittedReviewText = submittedReviewText,
        onSubmit = onSubmit,
    )
}
