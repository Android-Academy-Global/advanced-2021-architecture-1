package ru.gaket.themoviedb.presentation.auth.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.databinding.FragmentComposeBinding
import ru.gaket.themoviedb.presentation.auth.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
internal class ComposeAuthFragment : Fragment(R.layout.fragment_compose) {

    private val binding by viewBinding(FragmentComposeBinding::bind)

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setContent {
            AuthView(viewModel = viewModel, navigator = navigator)
        }
    }

    companion object {

        fun newInstance(): ComposeAuthFragment = ComposeAuthFragment()
    }
}