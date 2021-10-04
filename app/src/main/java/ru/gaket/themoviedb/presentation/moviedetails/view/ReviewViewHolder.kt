package ru.gaket.themoviedb.presentation.moviedetails.view

import android.content.Context
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.R.string
import ru.gaket.themoviedb.databinding.ItemReviewBinding
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview.MyReview
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview.SomeoneReview

class ReviewViewHolder(
    private val binding: ItemReviewBinding,
    private val onReviewClick: () -> Unit,
    private val onAddReviewClick: () -> Unit,
    private val onAuthorizeClick: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(review: MovieDetailsReview) = with(binding) {
        val isAddReviewView = review is MyReview && review.isPosted.not()
        val context = itemView.context

        gReviewContent.isVisible = isAddReviewView.not()
        gReviewAdd.isVisible = isAddReviewView

        if (isAddReviewView) {
            val isAuthorized = review is MyReview && review.isAuthorized

            tvReviewAddLabelLabel.text = getTextForAddReviewButton(isAuthorized, context)
            cvReviewCard.setOnClickListener { getAddReviewClickListener(isAuthorized).invoke() }
        } else {
            cvReviewCard.setOnClickListener { onReviewClick.invoke() }
            setupReviewView(context, review)
        }
    }

    private fun getTextForAddReviewButton(
        isAuthorized: Boolean,
        context: Context,
    ) = if (isAuthorized) {
        context.getString(string.review_add_label)
    } else {
        context.getString(string.authiorize_to_add_review_label)
    }

    private fun getAddReviewClickListener(isAuthorized: Boolean) = if (isAuthorized) {
        onAddReviewClick
    } else {
        onAuthorizeClick
    }

    private fun setupReviewView(context: Context, review: MovieDetailsReview) = with(binding) {
        tvReviewUserName.text = when (review) {
            is MyReview -> context.getString(R.string.review_my_review)
            is SomeoneReview -> review.userName
        }

        rbReviewRating.rating = review.review?.rating?.starsCount?.toFloat() ?: 0.0f
        tvReviewLiked.text = review.review?.liked.orEmpty()
        tvReviewDisliked.text = review.review?.disliked.orEmpty()
    }
}
