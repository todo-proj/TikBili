package com.benyq.tikbili.player.playback.layer.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.benyq.tikbili.base.utils.L

/**
 *
 * @author benyq
 * @date 4/7/2024
 *
 */
abstract class AnimateLayer : BaseLayer() {

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

    private var _animateShowListener: Animator.AnimatorListener? = null
    private var _animateDismissListener: Animator.AnimatorListener? = null

    open fun setAnimateShowListener(listener: Animator.AnimatorListener?) {
        _animateShowListener = listener
    }

    open fun setAnimateDismissListener(listener: Animator.AnimatorListener?) {
        _animateDismissListener = listener
    }

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
        L.v(this, "animateShow", "start")
        show()
        if (!isShowing()) return
        _animator.removeAllListeners()
        _animator.startDelay = startDelay
        _animator.duration = duration
        _animator.setTarget(getView())
        initAnimateShowProperty(_animator)
        _animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                resetViewAnimateProperty()
                L.v(this, "animateShow", "cancel")
            }

            override fun onAnimationEnd(animation: Animator) {
                L.v(this, "animateShow", "end")
                resetViewAnimateProperty()
                setState(State.IDLE)
            }
        })
        _animator.start()
        if (showListener != null) {
            _animator.addListener(showListener)
        }
        if (_animateShowListener != null) {
            _animator.addListener(_animateShowListener)
        }
        setState(State.SHOWING)
        if (autoDismiss) {
            postDismissRunnable()
        }
    }

    open fun requestAnimateDismiss(reason: String) {
        animateDismiss()
    }

    fun animateDismiss(
        startDelay: Long = 0,
        duration: Long = DEFAULT_ANIMATE_DURATION,
        dismissListener: Animator.AnimatorListener? = null,
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
        L.v(this, "animateDismiss", "start")
        _animator.removeAllListeners()
        _animator.startDelay = startDelay
        _animator.duration = duration
        _animator.setTarget(getView())
        initAnimateDismissProperty(_animator)
        _animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                resetViewAnimateProperty()
                L.v(this, "animateDismiss", "cancel")
            }

            override fun onAnimationEnd(animation: Animator) {
                dismiss()
                L.v(this, "animateDismiss", "end")
            }

        })
        _animator.start()
        if (dismissListener != null) {
            _animator.addListener(dismissListener)
        }
        if (_animateDismissListener != null) {
            _animator.addListener(_animateDismissListener)
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