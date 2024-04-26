package com.benyq.tikbili.scene.horicontal.layer

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.benyq.tikbili.player.playback.VideoLayer
import com.benyq.tikbili.player.playback.VideoLayerHost
import com.benyq.tikbili.player.playback.VideoView
import com.benyq.tikbili.player.playback.layer.base.BaseLayer
import java.lang.ref.WeakReference

/**
 *
 * @author benyq
 * @date 4/26/2024
 *
 */
class GestureLayer: BaseLayer() {
    override val tag: String = "GestureLayer"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateLayerView(parent: ViewGroup): View {
        val view = View(parent.context)
        val gesture = Gesture(parent.context, this)
        view.setOnTouchListener { v, event -> gesture.onTouchEvent(v, event) }
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return view
    }


    override fun requestDismiss(reason: String) {

    }

    override fun requestHide(reason: String) {

    }

    override fun onBindLayerHost(layerHost: VideoLayerHost) {
        super.onBindLayerHost(layerHost)
        show()
    }

    override fun onBindVideoView(videoView: VideoView) {
        super.onBindVideoView(videoView)
        show()
    }


    fun showController() {
        val layerHost = layerHost() ?: return
        val titleBarLayer = layerHost.findLayer(TitleBarLayer::class.java)
        val timeProgressBarLayer = layerHost.findLayer(TimeProgressBarLayer::class.java)
        titleBarLayer?.animateShow(true)
        timeProgressBarLayer?.animateShow(true)
    }

    fun dismissController() {
        val layerHost = layerHost() ?: return
        val titleBarLayer = layerHost.findLayer(TitleBarLayer::class.java)
        val timeProgressBarLayer = layerHost.findLayer(TimeProgressBarLayer::class.java)
        titleBarLayer?.animateDismiss()
        timeProgressBarLayer?.animateDismiss()
    }

    fun toggleControllerVisibility() {
        if (isControllerShowing()) {
            dismissController()
        } else {
            showController()
        }
    }

    fun isControllerShowing(): Boolean {
        val layerHost = layerHost() ?: return false
        val titleBarLayer = layerHost.findLayer(TitleBarLayer::class.java) ?: return false
        return titleBarLayer.isShowing()
    }


    class Gesture(context: Context, gestureLayer: GestureLayer): GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        private val gestureDetector = GestureDetector(context, this)
        private val weakRef = WeakReference(gestureLayer)

        fun onTouchEvent(view: View, event: MotionEvent) : Boolean {
            var handle = gestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                handle = onUp(event) || handle
            } else if (event.action == MotionEvent.ACTION_CANCEL) {
                handle = onCancel(event) || handle
            }
            return handle
        }

        private fun check(): Boolean {
            val gestureLayer = weakRef.get() ?: return false
            val player = gestureLayer.player() ?: return false
            return player.isInPlaybackState()
        }

        override fun onDown(e: MotionEvent): Boolean {
            return check()
        }

        override fun onShowPress(e: MotionEvent) {

        }

        override fun onSingleTapUp(e: MotionEvent) = false

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float,
        ): Boolean {
            if (!check()) return false

            return true
        }

        override fun onLongPress(e: MotionEvent) {}

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float,
        ) = false

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val gestureLayer = weakRef.get() ?: return false
            gestureLayer.toggleControllerVisibility()
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (!check()) return false
            val gestureLayer = weakRef.get() ?: return false
            val player = gestureLayer.player() ?: return false
            if (player.isInPlaybackState()) {
                if (player.isPlaying()) {
                    player.pause()
                }else {
                    player.start()
                }
                return true
            }
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent) = false

        private fun onUp(e: MotionEvent?): Boolean {
            return false
        }

        private fun onCancel(e: MotionEvent?): Boolean {
            return false
        }


    }
}