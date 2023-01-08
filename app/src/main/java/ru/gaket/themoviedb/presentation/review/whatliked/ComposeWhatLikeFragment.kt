package ru.gaket.themoviedb.presentation.review.whatliked

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
import javax.inject.Inject

@AndroidEntryPoint
internal class ComposeWhatLikeFragment : Fragment(R.layout.fragment_compose) {

    private val binding by viewBinding(FragmentComposeBinding::bind)

    private val createReviewScopedRepository: CreateReviewScopedRepository by viewModels<CreateReviewScopedRepositoryImpl>(
        ownerProducer = { requireParentFragment() }
    )

    @Inject
    lateinit var viewModelAssistedFactory: WhatLikeViewModel.Factory

    private val viewModel: WhatLikeViewModel by viewModels {
        createAbstractViewModelFactory {
            viewModelAssistedFactory.create(createReviewScopedRepository = createReviewScopedRepository)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setContent {
            WhatLikeView(viewModel = viewModel)
        }
    }
}