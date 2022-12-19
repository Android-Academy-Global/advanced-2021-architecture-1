package ru.gaket.themoviedb.presentation.moviedetails.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.AuthScreen
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.core.navigation.ReviewScreen
import ru.gaket.themoviedb.core.navigation.WebNavigator
import ru.gaket.themoviedb.databinding.FragmentMovieDetailsBinding
import ru.gaket.themoviedb.presentation.moviedetails.model.getCalendarYear
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsEvent
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsState
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import ru.gaket.themoviedb.util.createAbstractViewModelFactory
import ru.gaket.themoviedb.util.showSnackbar
import javax.inject.Inject

@AndroidEntryPoint
class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    private val binding by viewBinding(FragmentMovieDetailsBinding::bind)

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var webNavigator: WebNavigator

    @Inject
    lateinit var viewModelAssistedFactory: MovieDetailsViewModel.Factory

    private val viewModel: MovieDetailsViewModel by viewModels {
        createAbstractViewModelFactory {
            viewModelAssistedFactory.create(
                movieId = requireArguments().getLong(ARG_MOVIE_ID),
                title = requireArguments().getString(ARG_MOVIE_TITLE).orEmpty()
            )
        }
    }

//    private val reviewsAdapter by lazy(LazyThreadSafetyMode.NONE) {
//        ReviewsAdapter(onReviewClick = viewModel::onReviewClick)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.state.observe(viewLifecycleOwner, ::render)
//        viewModel.events.observe(viewLifecycleOwner, ::handleEvent)

        setupListeners()
        setupReviewsList()
        setupPoster()
    }

    private fun render(state: MovieDetailsState) =
        when (state) {
            is MovieDetailsState.Loading -> showLoading(show = true)
            is MovieDetailsState.Result -> renderResult(state)
            is MovieDetailsState.Error -> view?.showSnackbar(R.string.error_getting_movie_info)
        }

    private fun handleEvent(event: MovieDetailsEvent) =
        when (event) {
            is MovieDetailsEvent.OpenScreen -> handleOpenScreenEvent(event)
        }

    private fun renderResult(state: MovieDetailsState.Result) {
        showLoading(show = false)

        binding.tvMovieDetailsTitle.text = state.movie.title
        binding.tvMovieDetailsYear.text = state.movie.releaseDate.getCalendarYear()?.toString().orEmpty()
        binding.tvMovieDetailsGenres.text = state.movie.genres
        binding.tvMovieDetailsOverview.text = state.movie.overview
        binding.tvMovieDetailsRating.text = state.movie.rating.toString()
        updatePoster(state.movie.thumbnail)
//        reviewsAdapter.submitList(state.allReviews)
    }

    private fun showLoading(show: Boolean) {
        binding.pbMovieDetailsInfo.isVisible = show
        binding.gMovieDetailsInfo.isVisible = !show

        binding.rvMovieDetailsReviews.isVisible = !show
    }

    private fun handleOpenScreenEvent(event: MovieDetailsEvent.OpenScreen) {
        val nextScreen = when (event) {
            is MovieDetailsEvent.OpenScreen.Auth -> AuthScreen()
            is MovieDetailsEvent.OpenScreen.Review -> ReviewScreen(viewModel.movieId)
        }

        navigator.navigateTo(nextScreen)
    }

    private fun setupListeners() {
        binding.ivMovieDetailsBack.setOnClickListener {
            navigator.back()
        }
        binding.ivMovieDetailsBrowse.setOnClickListener {
            webNavigator.navigateTo(viewModel.movieId)
        }
    }

    private fun setupReviewsList() {
//        binding.rvMovieDetailsReviews.adapter = reviewsAdapter
    }

    private fun setupPoster() {
        binding.ivMoviePoster.clipToOutline = true
    }

    private fun updatePoster(posterUrl: String?) {
        if (binding.ivMoviePoster.tag != posterUrl) {
            binding.ivMoviePoster.tag = posterUrl

            Picasso.get()
                .load(posterUrl)
                .placeholder(R.drawable.ph_movie_grey_200)
                .error(R.drawable.ph_movie_grey_200)
                .fit()
                .centerCrop()
                .into(binding.ivMoviePoster)
        }
    }

    companion object {

        private const val ARG_MOVIE_ID = "ARG_MOVIE_ID"
        private const val ARG_MOVIE_TITLE = "ARG_MOVIE_TITLE"

        fun newInstance(movieId: Long, title: String): MovieDetailsFragment = MovieDetailsFragment()
            .apply {
                arguments = bundleOf(
                    ARG_MOVIE_ID to movieId,
                    ARG_MOVIE_TITLE to title,
                )
            }
    }
}