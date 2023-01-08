package ru.gaket.themoviedb.presentation.review.whatnotliked

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.presentation.review.AbstractReviewTextViewModel

class WhatNotLikeViewModel @AssistedInject constructor(
    @Assisted private val createReviewScopedRepository: CreateReviewScopedRepository,
) : AbstractReviewTextViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(createReviewScopedRepository: CreateReviewScopedRepository): WhatNotLikeViewModel
    }

    override fun submitTextSuccess(text: String) {
        createReviewScopedRepository.setWhatDidNotLike(text)
    }
}