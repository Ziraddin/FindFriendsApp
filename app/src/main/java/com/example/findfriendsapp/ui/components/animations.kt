package com.example.findfriendsapp.ui.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable

@Composable
fun slideInFromLeftWithDelay(
    delay: Int = 0, duration: Int = 300, easing: Easing = LinearOutSlowInEasing
): EnterTransition {
    return fadeIn(
        animationSpec = AnimationWithDelay(duration = duration, delay = delay, easing = easing)
    ) + slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = AnimationWithDelay(duration = duration, delay = delay, easing = easing)
    )
}

@Composable
fun slideOutToRightWithDelay(
    delay: Int = 0, duration: Int = 300, easing: Easing = LinearOutSlowInEasing
): ExitTransition {
    return fadeOut(
        animationSpec = AnimationWithDelay(duration = duration, delay = delay, easing = easing)
    ) + slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = AnimationWithDelay(duration = duration, delay = delay, easing = easing)
    )
}

@Composable
fun <T> AnimationWithDelay(
    duration: Int = 300, delay: Int = 0, easing: Easing = LinearOutSlowInEasing
): FiniteAnimationSpec<T> {
    return tween(
        durationMillis = duration, delayMillis = delay, easing = easing
    )
}
