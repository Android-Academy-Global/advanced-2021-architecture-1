package ru.gaket.themoviedb.presentation.review

import ru.gaket.themoviedb.util.EMPTY

data class ReviewTextState(
    val submittedText: String = String.EMPTY,
    val isEmptyText: Boolean = false,
)