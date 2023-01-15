package ru.gaket.themoviedb.presentation.review

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.MovieDetailsScreen
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.databinding.FragmentReviewBinding
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.review.models.CreateReviewStep
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.presentation.review.rating.ComposeRatingFragment
import ru.gaket.themoviedb.presentation.review.whatliked.ComposeWhatLikeFragment
import ru.gaket.themoviedb.presentation.review.whatnotliked.ComposeWhatNotLikeFragment
import ru.gaket.themoviedb.util.createAbstractViewModelFactory
import javax.inject.Inject

@AndroidEntryPoint
class ReviewFragment : Fragment(R.layout.fragment_review) {

    private val binding: FragmentReviewBinding by viewBinding(FragmentReviewBinding::bind)

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var createReviewRepositoryAssistedFactory: CreateReviewScopedRepositoryImpl.Factory

    @Inject
    lateinit var reviewViewModelAssistedFactory: ReviewViewModel.Factory

    private val createReviewScopedRepository: CreateReviewScopedRepository by viewModels<CreateReviewScopedRepositoryImpl> {
        createAbstractViewModelFactory {
            createReviewRepositoryAssistedFactory.create(movieId = requireArguments().getLong(ARG_MOVIE_ID))
        }
    }

    private val viewModel: ReviewViewModel by viewModels<ReviewViewModel> {
        createAbstractViewModelFactory {
            reviewViewModelAssistedFactory.create(createReviewScopedRepository = createReviewScopedRepository)
        }
    }

    private val backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() = viewModel.toPreviousStep()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cvReviewSummary.setContent {
            ReviewView(viewModel = viewModel)
        }

        // TODO Move into ReviewView while refactoring navigation
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect(::handleState)
        }
    }

    private fun handleState(state: ReviewViewModel.State) {
        state.createState?.step?.let(::handleStep)
    }

    private fun handleStep(step: CreateReviewStep) {
        @IdRes val containerId = binding.containerReview.id

        val currentFragmentClass: Class<Fragment>? = childFragmentManager.findFragmentById(containerId)?.javaClass
        val newFragmentClass: Class<out Fragment>? = when (step) {
            CreateReviewStep.WHAT_LIKED -> ComposeWhatLikeFragment::class.java
            CreateReviewStep.WHAT_NOT_LIKED -> ComposeWhatNotLikeFragment::class.java
            CreateReviewStep.RATING -> ComposeRatingFragment::class.java
            CreateReviewStep.FINISH -> null
        }

        when {
            (newFragmentClass == null) -> {
                navigator.backTo(MovieDetailsScreen.TAG)
            }
            (currentFragmentClass != newFragmentClass) -> {
                childFragmentManager.commit {
                    replace(containerId, newFragmentClass, null)
                }
            }
        }
    }

    companion object {

        private const val ARG_MOVIE_ID = "ARG_MOVIE_ID"

        fun newInstance(movieId: MovieId): ReviewFragment =
            ReviewFragment().apply {
                arguments = bundleOf(ARG_MOVIE_ID to movieId)
            }
    }
}
