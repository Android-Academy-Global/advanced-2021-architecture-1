package ru.gaket.themoviedb.presentation.review.whatliked

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.presentation.review.common.ReviewTextView

@Preview
@Composable
private fun WhatLikeViewPreview() {
    WhatLikeView(
        submittedReviewText = "Some review",
        onSubmit = {}
    )
}

@Composable
internal fun WhatLikeView(viewModel: WhatLikeViewModel) {
    val submittedReviewText by viewModel.initialValue.observeAsState("")
    WhatLikeView(
        submittedReviewText = submittedReviewText,
        onSubmit = viewModel::submitInfo,
    )
}

@Composable
private fun WhatLikeView(
    submittedReviewText: String,
    onSubmit: (reviewText: String) -> Unit,
) {
    ReviewTextView(
        title = stringResource(id = R.string.review_what_did_you_like),
        submittedReviewText = submittedReviewText,
        onSubmit = onSubmit,
    )
}