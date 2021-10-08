package ru.gaket.themoviedb.presentation.review.whatliked

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.databinding.FragmentReviewTextBinding
import ru.gaket.themoviedb.presentation.review.CreateReviewRepoViewModel
import ru.gaket.themoviedb.presentation.review.ReviewFieldEvent
import ru.gaket.themoviedb.util.createAbstractViewModelFactory
import ru.gaket.themoviedb.util.showSnackbar
import javax.inject.Inject

@AndroidEntryPoint
class WhatLikeFragment : Fragment(R.layout.fragment_review_text) {

    private val binding: FragmentReviewTextBinding by viewBinding(FragmentReviewTextBinding::bind)

    private val sharedRepoViewModel: CreateReviewRepoViewModel by viewModels(ownerProducer = { requireParentFragment() })

    @Inject
    lateinit var viewModelAssistedFactory: WhatLikeViewModel.Factory

    private val viewModel: WhatLikeViewModel by viewModels {
        createAbstractViewModelFactory {
            viewModelAssistedFactory.create(createReviewRepository = sharedRepoViewModel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvReviewMessage.setText(R.string.review_what_did_you_like)
            btnNext.setOnClickListener {
                viewModel.submitInfo(etReviewField.text.toString())
            }
        }

        viewModel.events.observe(viewLifecycleOwner, ::handleState)
        viewModel.initialValue.observe(viewLifecycleOwner) { initialValue ->
            binding.etReviewField.setText(initialValue)
        }
    }

    private fun handleState(reviewErrorField: ReviewFieldEvent) =
        when (reviewErrorField) {
            ReviewFieldEvent.EMPTY_FIELD -> requireView().showSnackbar(R.string.review_error_should_not_be_empty)
        }
}