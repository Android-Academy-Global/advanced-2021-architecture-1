package ru.gaket.themoviedb.presentation.moviedetails.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun LikeAnimationCompose(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    animationFinished: () -> Unit = {}
) {
    val size = remember {
        Animatable(
            initialValue = 0.dp,
            typeConverter = Dp.VectorConverter
        )
    }

    val position = remember {
        Animatable(
            initialValue = 0.dp,
            typeConverter = Dp.VectorConverter
        )
    }


    LaunchedEffect(isActive) {
        if (isActive) {
            // 1) Expand Heart from initial state
            size.animateTo(
                targetValue = 300.dp,
                animationSpec = tween(500)
            )

            val springSpec: SpringSpec<Dp> = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
            // 2) Imitate heartbeat
            repeat(3) {
                size.animateTo(250.dp, springSpec)
                size.animateTo(300.dp, springSpec)
            }

            // 3) Shrink and hide heart to top
            coroutineScope {
                launch {
                    size.animateTo(0.dp, tween(800))
                }
                launch {
                    position.animateTo((-500).dp, tween(800))
                }
            }

            // 4) Reset to initial state
            size.snapTo(0.dp)
            position.snapTo(0.dp)

            animationFinished()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(size.value)
                .offset(y = position.value),
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.Red
        )
    }
}