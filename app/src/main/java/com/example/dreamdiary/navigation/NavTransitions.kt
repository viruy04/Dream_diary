package com.example.dreamdiary.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import androidx.navigation.NavBackStackEntry

/** Все длительности и easing анимаций навигации - меняются в одном месте. */
object NavMotion {
    // Слайд между обычными экранами
    const val SlideInMs = 320
    const val SlideOutMs = 220
    const val SlideOutFadeMs = 120       // старый экран гаснет быстрее, чем уезжает
    const val SlideInFadeDelayMs = 60    // экраны прозрачные: небольшая задержка убирает наложение текстов
    const val SlideOffsetFraction = 0.28f

    // Fade-through (вкладки нижней панели, онбординг -> главная)
    const val FadeThroughOutMs = 90
    const val FadeThroughInMs = 210
    const val FadeThroughScaleFrom = 0.96f

    // Нижняя панель
    const val BottomBarInMs = 250
    const val BottomBarOutMs = 200

    val Ease: Easing = FastOutSlowInEasing
}

/** Группы маршрутов, от которых зависит тип перехода. */
object NavRoutes {
    /** Вкладки нижней панели. */
    val tabs: Set<String> = setOf(
        Destination.Home.route,
        Destination.Dreams.route,
        Destination.Profile.route
    )

    /** Экраны онбординга. В проекте их пока нет - добавьте сюда маршруты, когда появятся. */
    val onboarding: Set<String> = emptySet()

    /** Экраны, на которых нижняя панель скрыта. */
    val bottomBarHidden: Set<String> = setOf(
        Destination.Calendar.route,
        Destination.Statistics.route,
        Destination.Constellation.route,
        Destination.Achievements.route
    )
}

// ---------- Выбор типа перехода ----------

private fun AnimatedContentTransitionScope<NavBackStackEntry>.useFadeThrough(): Boolean {
    val from = initialState.destination.route
    val to = targetState.destination.route
    val tabSwitch = from in NavRoutes.tabs && to in NavRoutes.tabs
    val onboardingToMain = from in NavRoutes.onboarding && to in NavRoutes.tabs
    return tabSwitch || onboardingToMain
}

// ---------- Fade-through ----------

fun fadeThroughEnter(): EnterTransition {
    val delay = NavMotion.FadeThroughOutMs
    return fadeIn(
        tween(NavMotion.FadeThroughInMs, delayMillis = delay, easing = NavMotion.Ease)
    ) + scaleIn(
        initialScale = NavMotion.FadeThroughScaleFrom,
        animationSpec = tween(NavMotion.FadeThroughInMs, delayMillis = delay, easing = NavMotion.Ease)
    )
}

fun fadeThroughExit(): ExitTransition =
    fadeOut(tween(NavMotion.FadeThroughOutMs, easing = NavMotion.Ease))

// ---------- Слайд вперёд ----------

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideForwardEnter(): EnterTransition =
    slideIntoContainer(
        towards = SlideDirection.Left,
        animationSpec = tween(NavMotion.SlideInMs, easing = NavMotion.Ease),
        initialOffset = { fullWidth -> (fullWidth * NavMotion.SlideOffsetFraction).toInt() }
    ) + fadeIn(
        tween(
            durationMillis = NavMotion.SlideInMs - NavMotion.SlideInFadeDelayMs,
            delayMillis = NavMotion.SlideInFadeDelayMs,
            easing = NavMotion.Ease
        )
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideForwardExit(): ExitTransition =
    slideOutOfContainer(
        towards = SlideDirection.Left,
        animationSpec = tween(NavMotion.SlideOutMs, easing = NavMotion.Ease),
        targetOffset = { fullWidth -> (fullWidth * NavMotion.SlideOffsetFraction).toInt() }
    ) + fadeOut(tween(NavMotion.SlideOutFadeMs, easing = NavMotion.Ease))

// ---------- Слайд назад (зеркально) ----------

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideBackEnter(): EnterTransition =
    slideIntoContainer(
        towards = SlideDirection.Right,
        animationSpec = tween(NavMotion.SlideInMs, easing = NavMotion.Ease),
        initialOffset = { fullWidth -> (fullWidth * NavMotion.SlideOffsetFraction).toInt() }
    ) + fadeIn(
        tween(
            durationMillis = NavMotion.SlideInMs - NavMotion.SlideInFadeDelayMs,
            delayMillis = NavMotion.SlideInFadeDelayMs,
            easing = NavMotion.Ease
        )
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideBackExit(): ExitTransition =
    slideOutOfContainer(
        towards = SlideDirection.Right,
        animationSpec = tween(NavMotion.SlideOutMs, easing = NavMotion.Ease),
        targetOffset = { fullWidth -> (fullWidth * NavMotion.SlideOffsetFraction).toInt() }
    ) + fadeOut(tween(NavMotion.SlideOutFadeMs, easing = NavMotion.Ease))

// ---------- То, что передаётся в NavHost ----------

fun AnimatedContentTransitionScope<NavBackStackEntry>.navEnter(): EnterTransition =
    if (useFadeThrough()) fadeThroughEnter() else slideForwardEnter()

fun AnimatedContentTransitionScope<NavBackStackEntry>.navExit(): ExitTransition =
    if (useFadeThrough()) fadeThroughExit() else slideForwardExit()

fun AnimatedContentTransitionScope<NavBackStackEntry>.navPopEnter(): EnterTransition =
    if (useFadeThrough()) fadeThroughEnter() else slideBackEnter()

fun AnimatedContentTransitionScope<NavBackStackEntry>.navPopExit(): ExitTransition =
    if (useFadeThrough()) fadeThroughExit() else slideBackExit()

// ---------- Нижняя панель ----------

fun bottomBarEnter(): EnterTransition =
    slideInVertically(
        animationSpec = tween(NavMotion.BottomBarInMs, easing = NavMotion.Ease),
        initialOffsetY = { fullHeight -> fullHeight }
    ) + fadeIn(
        tween(NavMotion.BottomBarInMs, easing = NavMotion.Ease)
    ) + expandVertically(
        animationSpec = tween(NavMotion.BottomBarInMs, easing = NavMotion.Ease),
        expandFrom = Alignment.Top
    )

fun bottomBarExit(): ExitTransition =
    slideOutVertically(
        animationSpec = tween(NavMotion.BottomBarOutMs, easing = NavMotion.Ease),
        targetOffsetY = { fullHeight -> fullHeight }
    ) + fadeOut(
        tween(NavMotion.BottomBarOutMs, easing = NavMotion.Ease)
    ) + shrinkVertically(
        animationSpec = tween(NavMotion.BottomBarOutMs, easing = NavMotion.Ease),
        shrinkTowards = Alignment.Top
    )