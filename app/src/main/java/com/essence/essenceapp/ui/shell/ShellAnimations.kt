package com.essence.essenceapp.ui.shell

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

const val SHELL_TAB_ENTER_DURATION_MS = 190
const val SHELL_TAB_EXIT_DURATION_MS = 180
const val SHELL_POP_ENTER_DURATION_MS = 220
const val SHELL_POP_EXIT_DURATION_MS = 190
const val SHELL_BOTTOM_BAR_ENTER_DURATION_MS = 260
const val SHELL_BOTTOM_BAR_EXIT_DURATION_MS = 190

private const val SHELL_BOTTOM_BAR_STIFFNESS = 220f

val ShellEmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
val ShellEmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

fun <T> shellSpring(): SpringSpec<T> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = SHELL_BOTTOM_BAR_STIFFNESS
)

val shellEnterTransition: EnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = SHELL_TAB_ENTER_DURATION_MS,
        easing = ShellEmphasizedDecelerate
    )
)

val shellExitTransition: ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = SHELL_TAB_EXIT_DURATION_MS,
        easing = ShellEmphasizedAccelerate
    )
)

val shellPopEnterTransition: EnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = SHELL_POP_ENTER_DURATION_MS,
        easing = ShellEmphasizedDecelerate
    )
)

val shellPopExitTransition: ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = SHELL_POP_EXIT_DURATION_MS,
        easing = ShellEmphasizedAccelerate
    )
)
