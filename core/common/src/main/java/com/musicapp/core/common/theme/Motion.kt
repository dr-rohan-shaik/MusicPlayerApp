package com.musicapp.core.common.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp

object MusicMotion {
    // Duration constants
    val Fast = 150
    val Medium = 250
    val Slow = 350
    val ExtraSlow = 500

    // Easing
    val StandardEasing = androidx.compose.animation.core.Easing.OutCubic
    val EmphasizedEasing = androidx.compose.animation.core.Easing.InOutCubic
    val DecelerateEasing = androidx.compose.animation.core.Easing.OutQuart
    val AccelerateEasing = androidx.compose.animation.core.Easing.InQuart

    // Tween specs
    val FastTween: AnimationSpec<Float> = tween(Fast, easing = StandardEasing)
    val MediumTween: AnimationSpec<Float> = tween(Medium, easing = StandardEasing)
    val SlowTween: AnimationSpec<Float> = tween(Slow, easing = EmphasizedEasing)
    val ExtraSlowTween: AnimationSpec<Float> = tween(ExtraSlow, easing = EmphasizedEasing)

    // Spring specs
    val GentleSpring: AnimationSpec<Float> = spring(
        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
    )
    val StiffSpring: AnimationSpec<Float> = spring(
        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioHighBouncy,
        stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
    )
    val CardPressSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.9f,
        stiffness = 1500f
    )

    // Specific animations
    val PlayPauseMorph = MediumTween
    val MiniplayerExpand = SlowTween
    val MiniplayerCollapse = MediumTween
    val CardPress = tween(100, easing = AccelerateEasing)
    val CardRelease = GentleSpring
    val BottomSheetExpand = MediumTween
    val BottomSheetCollapse = FastTween
    val TabIndicatorSlide = tween(200, easing = DecelerateEasing)
    val ShimmerSweep = tween(1500, easing = StandardEasing, delayMillis = 0, infinite = true)
    val FadeIn = tween(200, easing = StandardEasing)
    val FadeOut = tween(150, easing = AccelerateEasing)
    val ScaleIn = tween(200, easing = DecelerateEasing)
    val ScaleOut = tween(150, easing = AccelerateEasing)
    val SlideUp = tween(300, easing = DecelerateEasing)
    val SlideDown = tween(250, easing = AccelerateEasing)
    val CrossFade = tween(200, easing = StandardEasing)

    // Shared element transition
    val SharedElementTransition = tween(300, easing = EmphasizedEasing)

    // Progress/seek
    val SeekThumbExpand = tween(100, easing = DecelerateEasing)
    val SeekThumbCollapse = tween(100, easing = AccelerateEasing)
}