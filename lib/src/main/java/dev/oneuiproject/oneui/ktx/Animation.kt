package dev.oneuiproject.oneui.ktx

import android.animation.Animator
import android.view.animation.Animation

inline fun Animation.setListener(
    crossinline onStart: (animation: Animation) -> Unit = {},
    crossinline onEnd: (animation: Animation) -> Unit = {},
    crossinline onRepeat: (animation: Animation) -> Unit = {}
): Animation.AnimationListener {
    val listener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animator: Animation) = onRepeat(animator)
        override fun onAnimationEnd(animator: Animation) = onEnd (animator)
        override fun onAnimationStart(animator: Animation) = onStart(animator)
    }
    setAnimationListener(listener)
    return listener
}

/**
 * Add an action which will be invoked when the animation has ended.
 *
 * @return the [Animator.AnimatorListener] added to the Animator
 * @see Animator.end
 */
inline fun Animation.doOnEnd(
    crossinline action: (animation: Animation) -> Unit
): Animation.AnimationListener = setListener(onEnd = action)

/**
 * Add an action which will be invoked when the animation is started.
 *
 * @return the [Animator.AnimatorListener] added to the Animator
 * @see Animator.start
 */
inline fun Animation.doOnStart(
    crossinline action: (animation: Animation) -> Unit
): Animation.AnimationListener = setListener(onStart = action)
