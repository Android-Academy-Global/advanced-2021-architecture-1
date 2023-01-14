package ru.gaket.themoviedb.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

val <T> T.exhaustive: T
    get() = this

val String.Companion.EMPTY: String
    get() = ""
