package ru.gaket.themoviedb.presentation.review.rating

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.databinding.FragmentComposeBinding
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.presentation.review.CreateReviewScopedRepositoryImpl
import ru.gaket.themoviedb.util.createAbstractViewModelFactory
import ru.gaket.themoviedb.util.showSnackbar
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class ComposeRatingFragment : Fragment(R.layout.fragment_compose) {

    private val binding by viewBinding(FragmentComposeBinding::bind)

    private val createReviewScopedRepository: CreateReviewScopedRepository by viewModels<CreateReviewScopedRepositoryImpl>(
        ownerProducer = { requireParentFragment() }
    )

    @Inject
    lateinit var viewModelAssistedFactory: RatingViewModel.Factory

    private val viewModel: RatingViewModel by viewModels {
        createAbstractViewModelFactory {
            viewModelAssistedFactory.create(createReviewScopedRepository = createReviewScopedRepository)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setContent {
            ReviewRatingView(viewModel = viewModel)
        }

        // TODO Move into composition
        viewModel.event.observe(viewLifecycleOwner, ::processReviewEvent)
    }

    private fun processReviewEvent(event: RatingViewModel.Event) {
        Timber.d("Event received: $event")
        when (event) {
            RatingViewModel.Event.ERROR_UNKNOWN,
            RatingViewModel.Event.ERROR_USER_NOT_SIGNED,
            -> {
                requireView().showSnackbar(R.string.review_error_unknown)
            }
            RatingViewModel.Event.ERROR_ZERO_RATING -> {
                requireView().showSnackbar(R.string.review_error_zero_rating)
            }
        }
    }
}