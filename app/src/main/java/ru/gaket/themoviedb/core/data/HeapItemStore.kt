package ru.gaket.themoviedb.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.gaket.themoviedb.domain.review.store.ItemStore

class HeapItemStore<T : Any>(initialValue: T? = null) : ItemStore<T> {

    private val itemState = MutableStateFlow(initialValue)

    override var item: T?
        get() = itemState.value
        set(newValue) {
            itemState.value = newValue
        }

    override fun observeItem(): Flow<T?> =
        itemState

    override fun updateSafely(updateAction: (T) -> T) =
        itemState.update { prev ->
            if (prev != null) {
                updateAction(prev)
            } else {
                prev
            }
        }
}