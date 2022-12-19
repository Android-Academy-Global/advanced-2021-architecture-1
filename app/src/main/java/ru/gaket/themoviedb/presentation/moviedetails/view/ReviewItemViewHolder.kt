//package ru.gaket.themoviedb.presentation.moviedetails.view
//
//import android.content.Context
//import android.view.View
//import androidx.annotation.StringRes
//import androidx.recyclerview.widget.RecyclerView
//import ru.gaket.themoviedb.R
//import ru.gaket.themoviedb.databinding.ItemAddReviewBinding
//import ru.gaket.themoviedb.databinding.ItemExistingReviewBinding
//import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview
//
//sealed class ReviewItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
//
//class AddReviewViewHolder(
//    private val binding: ItemAddReviewBinding,
//    private val onReviewClick: (MovieDetailsReview.Add) -> Unit,
//) : ReviewItemViewHolder(binding.root) {
//
//    fun bind(item: MovieDetailsReview.Add) {
//        binding.tvReviewAddLabelLabel.text = binding.root.context.getTextForAddReviewButton(item.isAuthorized)
//        binding.root.setOnClickListener { onReviewClick(item) }
//    }
//}
//
//class ExistingReviewViewHolder(
//    private val binding: ItemExistingReviewBinding,
//    private val onReviewClick: (MovieDetailsReview) -> Unit,
//) : ReviewItemViewHolder(binding.root) {
//
//    fun bind(item: MovieDetailsReview.Existing) {
//        binding.tvReviewUserName.text = when (item) {
//            is MovieDetailsReview.Existing.My -> binding.root.context.getString(R.string.review_my_review)
//            is MovieDetailsReview.Existing.Someone -> item.info.author.value
//        }
//
//        binding.rbReviewRating.rating = item.review.rating.starsCount.toFloat()
//        binding.tvReviewLiked.text = item.review.liked
//        binding.tvReviewDisliked.text = item.review.disliked
//
//        binding.root.setOnClickListener { onReviewClick(item) }
//    }
//}
//
//private fun Context.getTextForAddReviewButton(isAuthorized: Boolean): String {
//    @StringRes val strResId = if (isAuthorized) {
//        R.string.review_add_label
//    } else {
//        R.string.authiorize_to_add_review_label
//    }
//
//    return this.getString(strResId)
//}