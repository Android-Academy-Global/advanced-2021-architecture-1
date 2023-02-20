package ru.gaket.themoviedb.presentation.review

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.review.models.*
import ru.gaket.themoviedb.domain.review.models.CreateReviewStep.FINISH
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.domain.review.store.ItemStore
import ru.gaket.themoviedb.util.Result
import ru.gaket.themoviedb.util.previous
import timber.log.Timber

class CreateReviewScopedRepositoryImpl @AssistedInject constructor(
    @Assisted override val movieId: MovieId,
    private val createReviewFormStore: ItemStore<CreateReviewState>,
) : ViewModel(), CreateReviewScopedRepository {

    init {
        Timber.d("CreateReviewScopedRepositoryImpl init")

        createReviewFormStore.item = CreateReviewState(
            form = CreateReviewForm.dummyReviewInstance(movieId),
            step = CreateReviewStep.RATING
        )
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("CreateReviewScopedRepositoryImpl onCleared")
    }

    override fun observeState(): Flow<CreateReviewState> =
        createReviewFormStore.observeItem()
            .filterNotNull()

    override fun setWhatLike(whatLiked: String) =
        createReviewFormStore.updateSafely { state ->
            state.copy(
                form = state.form.copy(whatLiked = whatLiked),
                step = CreateReviewStep.WHAT_NOT_LIKED
            )
        }

    override fun setWhatDidNotLike(whatDidNotLike: String) =
        createReviewFormStore.updateSafely { state ->
            state.copy(
                form = state.form.copy(whatDidNotLike = whatDidNotLike),
                step = CreateReviewStep.RATING
            )
        }

    override fun setRating(rating: Rating) =
        createReviewFormStore.updateSafely { state ->
            state.copy(form = state.form.copy(rating = rating))
        }

    override fun toPreviousStep() = createReviewFormStore.updateSafely { state ->
        val newStep = (state.step.previous() ?: CreateReviewStep.FINISH)
        state.copy(step = newStep)
    }

    override fun markAsFinished() =
        createReviewFormStore.updateSafely { state ->
            state.copy(step = FINISH)
        }

    override fun buildDraft(): Result<ReviewDraft, Throwable> {
        val form = createReviewFormStore.item?.form

        return if ((form != null)
            && (form.whatLiked != null)
            && (form.whatDidNotLike != null)
            && form.rating != null
        ) {
            Result.Success(ReviewDraft(
                movieId = form.movieId,
                liked = form.whatLiked,
                disliked = form.whatDidNotLike,
                rating = form.rating
            ))
        } else {
            Result.Error(IllegalStateException("Form not filled"))
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(movieId: MovieId): CreateReviewScopedRepositoryImpl
    }
}
