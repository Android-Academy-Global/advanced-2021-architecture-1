package ru.gaket.themoviedb.presentation.movies.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.MovieDetailsScreen
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.databinding.FragmentComposeBinding
import ru.gaket.themoviedb.presentation.movies.viewmodel.MoviesViewModel
import javax.inject.Inject

@AndroidEntryPoint
internal class ComposeMoviesFragment : Fragment(R.layout.fragment_compose) {

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentComposeBinding::bind)

    private val viewModel: MoviesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setContent {
            MoviesView(
                viewModel = viewModel,
                onMovieClick = { movie ->
                    navigator.navigateTo(MovieDetailsScreen(movie.id, movie.title))
                }
            )
        }
    }

    companion object {

        fun newInstance(): ComposeMoviesFragment = ComposeMoviesFragment()
    }
}
