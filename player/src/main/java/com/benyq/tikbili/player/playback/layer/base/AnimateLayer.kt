package com.benyq.tikbili.player.playback.layer.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.benyq.tikbili.player.playback.VideoLayer

/**
 *
 * @author benyq
 * @date 4/7/2024
 *
 */
abstract class AnimateLayer : VideoLayer() {

    companion object {
        const val DEFAULT_ANIMATE_DURATION: Long = 300
        const val DEFAULT_ANIMATE_DISMISS_DELAY: Long = 4000
    }

    private var _state = State.IDLE
    private val _animator: Animator by lazy {
        createAnimator()
    }
    private val handler = Handler(Looper.getMainLooper())
    private val animateDismissRunnable = Runnable { animateDismiss() }

    fun animateShow(
        autoDismiss: Boolean,
        startDelay: Long = 0,
        duration: Long = DEFAULT_ANIMATE_DURATION,
        showListener: Animator.AnimatorListener? = null,
    ) {
        removeDismissRunnable()
        when (_state) {
            State.SHOWING -> {
                if (autoDismiss) {
                    postDismissRunnable()
                }
            }

            State.DISMISSING -> {
                if (_animator.isStarted) {
                    _animator.cancel()
                }
            }

            else -> {
                if (isShowing()) {
                    if (autoDismiss) {
                        postDismissRunnable()
                    }
                    return
                }
            }
        }
        show()
        if (!isShowing()) return
        _animator.removeAllListeners()
        _animator.startDelay = startDelay
        _animator.duration = duration
        _animator.setTarget(getView())
        initAnimateShowProperty(_animator)
        _animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                Log.d("pauseLayer", "onAnimationStart: ")
            }
            override fun onAnimationCancel(animation: Animator) {
                resetViewAnimateProperty()
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.d("pauseLayer", "onAnimationEnd: ")
                resetViewAnimateProperty()
                setState(State.IDLE)
            }
        })
        _animator.start()
        if (showListener != null) {
            _animator.addListener(showListener)
        }
        setState(State.SHOWING)
        if (autoDismiss) {
            postDismissRunnable()
        }
    }

    fun animateDismiss(
        startDelay: Long = 0,
        duration: Long = DEFAULT_ANIMATE_DURATION,
        showListener: Animator.AnimatorListener? = null,
    ) {
        removeDismissRunnable()
        when (_state) {
            State.DISMISSING -> {
                return
            }

            State.SHOWING -> {
                if (_animator.isStarted) {
                    _animator.cancel()
                }
            }

            else -> {
                if (!isShowing()) {
                    return
                }
            }
        }
        _animator.removeAllListeners()
        _animator.startDelay = startDelay
        _animator.duration = duration
        _animator.setTarget(getView())
        initAnimateDismissProperty(_animator)
        _animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                Log.d("pauseLayer", "onAnimationStart: ")
            }
            override fun onAnimationCancel(animation: Animator) {
                resetViewAnimateProperty()
            }

            override fun onAnimationEnd(animation: Animator) {
                dismiss()
                Log.d("pauseLayer", "onAnimationEnd: ")
            }

        })
        _animator.start()
        if (showListener != null) {
            _animator.addListener(showListener)
        }
        setState(State.DISMISSING)
    }

    fun animateToggle(autoDismiss: Boolean) {
        when(_state) {
            State.IDLE -> {
                if (isShowing()) {
                    animateDismiss()
                } else {
                    animateShow(autoDismiss)
                }
            }
            State.SHOWING -> {
                animateDismiss()
            }
            State.DISMISSING -> {
                animateShow(autoDismiss)
            }
        }
    }

    override fun show() {
        removeDismissRunnable()
        if (_animator.isStarted) {
            _animator.removeAllListeners()
            _animator.cancel()
        }
        super.show()
        resetViewAnimateProperty()
        setState(State.IDLE)
    }

    override fun dismiss() {
        removeDismissRunnable()
        if (_animator.isStarted) {
            _animator.removeAllListeners()
            _animator.cancel()
        }
        super.dismiss()
        resetViewAnimateProperty()
        setState(State.IDLE)
    }

    protected fun createAnimator(): Animator {
        return ObjectAnimator().apply {
            setPropertyName("alpha")
        }
    }

    protected fun resetViewAnimateProperty() {
        getView()?.alpha = 1f
    }

    protected open fun initAnimateShowProperty(animator: Animator?) {
        if (animator is ObjectAnimator) {
            animator.setFloatValues(0f, 1f)
        }
    }

    protected open fun initAnimateDismissProperty(animator: Animator?) {
        if (animator is ObjectAnimator) {
            animator.setFloatValues(1f, 0f)
        }
    }

    private fun removeDismissRunnable() {
        handler.removeCallbacks(animateDismissRunnable)
    }

    private fun postDismissRunnable() {
        handler.postDelayed(animateDismissRunnable, DEFAULT_ANIMATE_DISMISS_DELAY)
    }

    private fun setState(state: State) {
        if (_state != state) {
            _state = state
        }
    }

    enum class State {
        IDLE,
        SHOWING,
        DISMISSING
    }
}