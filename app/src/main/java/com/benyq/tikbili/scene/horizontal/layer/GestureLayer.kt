package com.benyq.tikbili.scene.horizontal.layer

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.player.playback.VideoLayerHost
import com.benyq.tikbili.player.playback.VideoView
import com.benyq.tikbili.player.playback.layer.base.BaseLayer
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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
        val playPauseLayer = layerHost.findLayer(PlayPauseLayer::class.java)
        titleBarLayer?.animateShow(true)
        timeProgressBarLayer?.animateShow(true)
        playPauseLayer?.animateShow(true)
    }

    fun dismissController() {
        val layerHost = layerHost() ?: return
        val titleBarLayer = layerHost.findLayer(TitleBarLayer::class.java)
        val timeProgressBarLayer = layerHost.findLayer(TimeProgressBarLayer::class.java)
        val playPauseLayer = layerHost.findLayer(PlayPauseLayer::class.java)
        titleBarLayer?.animateDismiss()
        timeProgressBarLayer?.animateDismiss()
        playPauseLayer?.animateDismiss()
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
        companion object {
            private const val STATE_IDLE = 0
            private const val STATE_VOLUME = 1
            private const val STATE_BRIGHTNESS = 2
        }

        private var _state = STATE_IDLE
        private var brightnessProgress: Float = 0f
        private var brightnessProgressLast: Float = 0f

        private var volumeProgress: Float = 0f
        private var volumeProgressLast: Float = 0f

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
            val gestureLayer = weakRef.get() ?: return false

            val view = gestureLayer.getView() ?: return false
            val viewWidth = view.width / 2

            if (_state == STATE_IDLE) {
                if (abs(distanceX) < abs(distanceY)) {
                    if ((e1?.x ?: 0f) < viewWidth / 2f) {
                        //brightness
                        startChangeBrightness()
                        setState(STATE_BRIGHTNESS)
                    }else {
                        // volume
                        startChangeVolume()
                        setState(STATE_VOLUME)
                    }
                }
            }
            when(_state) {
                STATE_IDLE -> Unit
                STATE_BRIGHTNESS -> {
                    changeBrightness(distanceY)
                }
                STATE_VOLUME -> {
                    changeVolume(distanceY)
                }
            }


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
            handleUpAndCancel()
            return false
        }

        private fun onCancel(e: MotionEvent?): Boolean {
            handleUpAndCancel()
            return false
        }

        private fun startChangeBrightness() {
            L.v(this, "startChangeBrightness")
            brightnessProgress = 0f
            brightnessProgressLast = 0f
            val layer = weakRef.get()?.layerHost()?.findLayer(BrightnessVolumeLayer::class.java) ?: return
            layer.setType(BrightnessVolumeLayer.TYPE_BRIGHTNESS)
        }

        private fun startChangeVolume() {
            L.v(this, "startChangeVolume")
            volumeProgress = 0f
            volumeProgressLast = 0f
            val layer = weakRef.get()?.layerHost()?.findLayer(BrightnessVolumeLayer::class.java) ?: return
            layer.setType(BrightnessVolumeLayer.TYPE_VOLUME)
        }

        private fun changeBrightness(delta: Float) {
            val gestureLayer = weakRef.get() ?: return
            val view = gestureLayer.getView() ?: return
            val layer = gestureLayer.layerHost()?.findLayer(BrightnessVolumeLayer::class.java) ?: return
            val viewHeight = view.height
            val progressDelta =  delta / (viewHeight / 2f) * 100
            brightnessProgress += progressDelta
            val deltaWithLast: Int = (brightnessProgress - brightnessProgressLast).toInt()
            L.v(this, "changeBrightness","deltaWithLast", deltaWithLast)
            if (abs(deltaWithLast) >= 1) {
                val progress = layer.getBrightnessByProgress()
                var currentProgress = progress + deltaWithLast
                currentProgress = min(100, max(0, currentProgress))
                brightnessProgressLast += deltaWithLast
                L.v(this, "changeBrightness", currentProgress)
                layer.setBrightnessByProgress(currentProgress)
            }
            if (!layer.isShowing()) {
                layer.animateShow(false)
            }
        }
        private fun changeVolume(delta: Float) {
            val gestureLayer = weakRef.get() ?: return
            val view = gestureLayer.getView() ?: return
            val layer = gestureLayer.layerHost()?.findLayer(BrightnessVolumeLayer::class.java) ?: return
            val viewHeight = view.height
            val progressDelta =  delta / (viewHeight / 2f) * 100
            volumeProgress += progressDelta
            val deltaWithLast: Int = (volumeProgress - volumeProgressLast).toInt()
            L.v(this, "changeVolume","deltaWithLast", deltaWithLast)
            if (abs(deltaWithLast) >= 1) {
                val progress = layer.getVolumeByProgress()
                var currentProgress = progress + deltaWithLast
                currentProgress = min(100, max(0, currentProgress))
                brightnessProgressLast += deltaWithLast
                L.v(this, "changeBrightness", currentProgress)
                layer.setVolumeByProgress(currentProgress)
            }
            if (!layer.isShowing()) {
                layer.animateShow(false)
            }
        }

        private fun setState(state: Int) {
            _state = state
        }

        private fun handleUpAndCancel() {
            setState(STATE_IDLE)
            val layer = weakRef.get()?.layerHost()?.findLayer(BrightnessVolumeLayer::class.java)
            layer?.animateDismiss()
        }
    }
}