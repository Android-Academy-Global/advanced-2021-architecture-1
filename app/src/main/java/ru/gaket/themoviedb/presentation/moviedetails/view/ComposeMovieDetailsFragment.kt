package ru.gaket.themoviedb.presentation.moviedetails.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.AuthScreen
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.core.navigation.ReviewScreen
import ru.gaket.themoviedb.core.navigation.WebNavigator
import ru.gaket.themoviedb.databinding.FragmentComposeBinding
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import javax.inject.Inject

@AndroidEntryPoint
internal class ComposeMovieDetailsFragment : Fragment(R.layout.fragment_compose) {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var webNavigator: WebNavigator

    private val viewModel: MovieDetailsViewModel by viewModels()

    private val binding by viewBinding(FragmentComposeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setContent {
            MovieDetailsView(
                movieId = requireArguments().getLong(ARG_MOVIE_ID),
                loadingTitle = requireArguments().getString(ARG_MOVIE_TITLE).orEmpty(),
                viewModel = viewModel,
                onNavigateToAuthScreen = {
                    navigator.navigateTo(AuthScreen())
                },
                onNavigateToReviewScreen = {
                    navigator.navigateTo(ReviewScreen(it))
                },
                onBackClick = navigator::back,
                onWebSearchClick = {
                    viewModel.movieId?.let {
                        webNavigator.navigateTo(it)
                    }
                }
            )
        }
    }

    companion object {

        private const val ARG_MOVIE_ID = "ARG_MOVIE_ID"
        private const val ARG_MOVIE_TITLE = "ARG_MOVIE_TITLE"

        fun newInstance(movieId: Long, title: String): ComposeMovieDetailsFragment =
            ComposeMovieDetailsFragment()
                .apply {
                    arguments = bundleOf(
                        ARG_MOVIE_ID to movieId,
                        ARG_MOVIE_TITLE to title,
                    )
                }
    }
}
