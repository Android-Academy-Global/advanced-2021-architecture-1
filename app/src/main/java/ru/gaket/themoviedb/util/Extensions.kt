package ru.gaket.themoviedb.util

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive

inline fun <reified T : Enum<T>> T.next(): T? {
    val values = enumValues<T>()
    return values.getOrNull(ordinal + 1)
}

inline fun <reified T : Enum<T>> T.previous(): T? {
    val values = enumValues<T>()
    return values.getOrNull(ordinal - 1)
}

inline fun <reified VM : ViewModel> createAbstractViewModelFactory(
    crossinline creator: () -> VM,
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == VM::class.java)
            @Suppress("UNCHECKED_CAST")
            return (creator() as T)
        }
    }

suspend fun <T> FlowCollector<T>.emitIfActive(value: T) {
    if (currentCoroutineContext().isActive) {
        emit(value)
    }
}

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.showSnackbar(
    @StringRes stringRes: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
) {
    Snackbar.make(this, stringRes, duration).show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.toGone() {
    visibility = View.GONE
}

fun View.toVisible() {
    visibility = View.VISIBLE
}

val <T> T.exhaustive: T
    get() = this

val String.Companion.EMPTY: String
    get() = ""