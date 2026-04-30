package com.essence.essenceapp.feature.song.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry

private val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
private val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
private val EmphasizedStandard = CubicBezierEasing(0.2f, 0f, 0f, 1f)

private const val ENTER_DURATION_MS = 300
private const val EXIT_DURATION_MS = 125
private const val SECONDARY_DURATION_MS = 140
private const val BACKGROUND_SCALE = 0.96f

fun AnimatedContentTransitionScope<NavBackStackEntry>.songDetailEnter(): EnterTransition {
    val slideSpec = tween<IntOffset>(durationMillis = ENTER_DURATION_MS, easing = EmphasizedDecelerate)
    val fadeSpec = tween<Float>(durationMillis = ENTER_DURATION_MS, easing = EmphasizedDecelerate)
    return slideInVertically(animationSpec = slideSpec) { fullHeight -> fullHeight } +
        fadeIn(animationSpec = fadeSpec)
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.songDetailPopExit(): ExitTransition {
    val slideSpec = tween<IntOffset>(durationMillis = EXIT_DURATION_MS, easing = EmphasizedAccelerate)
    val fadeSpec = tween<Float>(durationMillis = EXIT_DURATION_MS, easing = EmphasizedAccelerate)
    return slideOutVertically(animationSpec = slideSpec) { fullHeight -> fullHeight } +
        fadeOut(animationSpec = fadeSpec)
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.songDetailExit(): ExitTransition {
    val spec = tween<Float>(durationMillis = SECONDARY_DURATION_MS, easing = EmphasizedStandard)
    return fadeOut(animationSpec = spec) +
        scaleOut(animationSpec = spec, targetScale = BACKGROUND_SCALE)
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.songDetailPopEnter(): EnterTransition {
    val spec = tween<Float>(durationMillis = SECONDARY_DURATION_MS, easing = EmphasizedStandard)
    return fadeIn(animationSpec = spec) +
        scaleIn(animationSpec = spec, initialScale = BACKGROUND_SCALE)
}
