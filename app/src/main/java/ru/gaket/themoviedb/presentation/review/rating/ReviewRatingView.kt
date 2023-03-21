package ru.gaket.themoviedb.presentation.review.rating

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.presentation.review.common.RatingView
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import timber.log.Timber

@Composable
@Preview
private fun ReviewRatingViewPreview() {
    ReviewRatingView(RatingViewModel.State(),  {}, {})
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
internal fun ReviewRatingView(viewModel: RatingViewModel) {
//    val state by viewModel.state.collectAsStateWithLifecycle()
    val state2 by viewModel.state2.observeAsState()

    // TODO handle disabled state
    ReviewRatingView(
//        rating = state2?.rating ?: 0,
        state2!!,
//        error = state.error,
        onRatingChange = viewModel::onRatingChange,
        onSubmit = viewModel::submit,
    )
}

@Composable
fun <T> LiveData<T>.observeAsState(): State<T?> = observeAsState(value)

/**
 * Starts observing this [LiveData] and represents its values via [State]. Every time there would
 * be new value posted into the [LiveData] the returned [State] will be updated causing
 * recomposition of every [State.value] usage.
 *
 * The inner observer will automatically be removed when this composable disposes or the current
 * [LifecycleOwner] moves to the [Lifecycle.State.DESTROYED] state.
 *
 * @sample androidx.compose.runtime.livedata.samples.LiveDataWithInitialSample
 */
@Composable
fun <R, T : R> LiveData<T>.observeAsState(initial: R): State<R> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(initial) }
    DisposableEffect(this, lifecycleOwner) {
        val observer = Observer<T> {
            Log.e("PASH#","Observer: $it")
            state.value = it
        }
        observe(lifecycleOwner, observer)
        onDispose { removeObserver(observer) }
    }
    return state
}

@Composable
private fun ReviewRatingView(
//    rating: Int,
    state: RatingViewModel.State,
//    @StringRes error: Int?,
    onRatingChange: (rating: Int) -> Unit,
    onSubmit: () -> Unit,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
) {
//    if (error != null) {
//        val message = stringResource(error)
//
//        LaunchedEffect(snackbarState) {
//            snackbarState.showSnackbar(
//                message = message,
//            )
//        }
//    }

//    var rating2 by remember { mutableStateOf(0) }

    Column {
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = dimensionResource(id = R.dimen.space_normal)),
        ) {
            Text(
                text = stringResource(id = R.string.review_rating_message),
                fontSize = 24.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            RatingView(
                rating = state.rating,
                onRatingChange = onRatingChange,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = dimensionResource(id = R.dimen.space_normal)),
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.review_button_submit),
                )
            }
        }
        SnackbarHost(hostState = snackbarState)
    }
}
