//package ru.gaket.themoviedb.presentation.moviedetails.view
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.annotation.IntDef
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import ru.gaket.themoviedb.databinding.ItemAddReviewBinding
//import ru.gaket.themoviedb.databinding.ItemExistingReviewBinding
//import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview
//
//class ReviewsAdapter(
//    private val onReviewClick: (MovieDetailsReview) -> Unit,
//) : ListAdapter<MovieDetailsReview, ReviewItemViewHolder>(ReviewsDiffCallback()) {
//
//    @MovieDetailsReviewType
//    override fun getItemViewType(position: Int): Int =
//        when (getItem(position)) {
//            is MovieDetailsReview.Add -> VIEW_TYPE_ADD
//            is MovieDetailsReview.Existing -> VIEW_TYPE_EXISTING
//        }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        @MovieDetailsReviewType viewType: Int,
//    ): ReviewItemViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//
//        return when (viewType) {
//            VIEW_TYPE_ADD -> {
//                val binding = ItemAddReviewBinding.inflate(layoutInflater, parent, false)
//                AddReviewViewHolder(binding, onReviewClick)
//            }
//            VIEW_TYPE_EXISTING -> {
//                val binding = ItemExistingReviewBinding.inflate(layoutInflater, parent, false)
//                ExistingReviewViewHolder(binding, onReviewClick)
//            }
//            else -> {
//                throw IllegalArgumentException("Invalid viewType = $viewType")
//            }
//        }
//    }
//
//    override fun onBindViewHolder(holder: ReviewItemViewHolder, position: Int) {
//        val item = getItem(position)
//
//        when {
//            ((item is MovieDetailsReview.Add) && (holder is AddReviewViewHolder)) -> {
//                holder.bind(item)
//            }
//            ((item is MovieDetailsReview.Existing) && (holder is ExistingReviewViewHolder)) -> {
//                holder.bind(item)
//            }
//        }
//    }
//}
//
//@Retention(AnnotationRetention.SOURCE)
//@IntDef(
//    flag = true,
//    value = [VIEW_TYPE_ADD, VIEW_TYPE_EXISTING]
//)
//private annotation class MovieDetailsReviewType
//
//private const val VIEW_TYPE_ADD = 1
//private const val VIEW_TYPE_EXISTING = (VIEW_TYPE_ADD + 1)
//
//private class ReviewsDiffCallback : DiffUtil.ItemCallback<MovieDetailsReview>() {
//
//    override fun areItemsTheSame(oldItem: MovieDetailsReview, newItem: MovieDetailsReview): Boolean =
//        (oldItem.id == newItem.id)
//
//    override fun areContentsTheSame(oldItem: MovieDetailsReview, newItem: MovieDetailsReview): Boolean =
//        (oldItem == newItem)
//}