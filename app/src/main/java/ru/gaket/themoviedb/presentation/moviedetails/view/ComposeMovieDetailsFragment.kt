package ru.gaket.themoviedb.presentation.moviedetails.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.core.navigation.WebNavigator
import ru.gaket.themoviedb.databinding.FragmentComposeBinding
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import ru.gaket.themoviedb.util.createAbstractViewModelFactory
import javax.inject.Inject

internal class ComposeMovieDetailsFragment : Fragment(R.layout.fragment_compose) {

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

    private val binding by viewBinding(FragmentComposeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setContent {
            MovieDetailsView(
                viewModel = viewModel,
                navigator = navigator,
                webNavigator = webNavigator,
            )
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