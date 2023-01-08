package ru.gaket.themoviedb.presentation.review

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class AbstractReviewTextViewModel : ViewModel() {
    private val _reviewTextState = MutableStateFlow(ReviewTextState())
    val reviewTextState: StateFlow<ReviewTextState> = _reviewTextState
        .asStateFlow()

    abstract fun submitTextSuccess(text: String)

    fun onReviewTextChange(reviewText: String) {
        _reviewTextState.update { value ->
            value.copy(
                submittedText = reviewText,
                isEmptyText = false,
            )
        }
    }

    fun submitText() {
        _reviewTextState.update { value ->
            value.copy(
                isEmptyText = value.submittedText.isBlank(),
            )
        }

        if (!_reviewTextState.value.isEmptyText) {
            submitTextSuccess(_reviewTextState.value.submittedText)
        }
    }
}