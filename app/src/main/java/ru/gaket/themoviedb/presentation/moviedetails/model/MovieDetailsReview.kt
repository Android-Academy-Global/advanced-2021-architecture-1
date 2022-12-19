package ru.gaket.themoviedb.presentation.moviedetails.model

import ru.gaket.themoviedb.domain.review.models.Review
import ru.gaket.themoviedb.domain.review.models.SomeoneReview
import java.text.SimpleDateFormat
import java.util.*

sealed class MovieDetailsReview {

//    abstract val id: Id?

    data class Add(
        val textRes: Int,
    ) : MovieDetailsReview()
////    {
//
////        override val id: Id? get() = null
////    }
//
//    data class MyV2(
//        val review: Review,
//        val text: Int,
//    ) : MovieDetailsReview()
//
//    data class SomeoneV2(
//        val review: Review,
//        val text: String,
//    ) : MovieDetailsReview()

    sealed class Existing : MovieDetailsReview() {

        abstract val review: Review

//        final override val id: Id
//            get() = review.id

        data class My(
            override val review: Review,
            val textRes: Int,
        ) : Existing()

        data class Someone(
            val info: SomeoneReview,
            val text: String,
        ) : Existing() {

            override val review: Review get() = info.review
        }
    }
}

private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)

fun String.getCalendarYear(): Int? = try {
    DATE_FORMAT.parse(this)
} catch (e: Throwable) {
    null
}?.getCalendarYear()

private fun Date.getCalendarYear(): Int = Calendar.getInstance().run {
    time = this@getCalendarYear
    get(Calendar.YEAR)
}