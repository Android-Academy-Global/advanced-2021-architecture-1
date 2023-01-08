package ru.gaket.themoviedb.presentation.review.whatliked

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.presentation.review.AbstractReviewTextViewModel

class WhatLikeViewModel @AssistedInject constructor(
    @Assisted private val createReviewScopedRepository: CreateReviewScopedRepository,
) : AbstractReviewTextViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(createReviewScopedRepository: CreateReviewScopedRepository): WhatLikeViewModel
    }

    override fun submitTextSuccess(text: String) {
        createReviewScopedRepository.setWhatLike(text)
    }
}