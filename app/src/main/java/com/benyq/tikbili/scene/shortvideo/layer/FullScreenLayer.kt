package com.benyq.tikbili.scene.shortvideo.layer

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.benyq.tikbili.R
import com.benyq.tikbili.base.ext.px
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.player.PlayerEvent
import com.benyq.tikbili.player.playback.VideoLayer
import com.benyq.tikbili.scene.SceneEvent
import com.benyq.tikbili.scene.shortvideo.event.ActionCommentVisible
import com.benyq.tikbili.scene.shortvideo.event.ActionFullScreen
import com.benyq.tikbili.scene.shortvideo.event.ActionTrackProgressBar

/**
 *
 * @author benyq
 * @date 4/23/2024
 *
 */
class FullScreenLayer: VideoLayer() {
    override val tag: String = "FullScreenLayer"
    private var isInterceptShow = false

    override fun onCreateLayerView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_full_screen_layer, parent, false)
        val displayView = videoView()?.displayView()
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        displayView?.let {
            val topOffset = it.height + it.top
            layoutParams.topMargin = topOffset + 15.px
        }
        L.d(this, "onCreateLayerView", "topMargin", layoutParams.topMargin)
        view.layoutParams = layoutParams
        view.setOnClickListener {
            controller()?.dispatcher()?.obtain(ActionFullScreen::class.java)?.dispatch()
        }
        return view
    }

    override fun onBindPlaybackController(controller: PlaybackController?) {
        controller?.addPlaybackListener(listener)
    }

    override fun onUnbindPlaybackController(controller: PlaybackController?) {
        controller?.removePlaybackListener(listener)
    }

    override fun show() {
        if (isInterceptShow) return
        val dataSource = dataSource() ?: return
        if (dataSource.displayAspectRatio < 1f) return
        super.show()
    }

    private val listener = object: EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when(event.code) {
                PlayerEvent.Info.VIDEO_RENDERING_START -> {
                    show()
                }
                SceneEvent.Action.PROGRESS_BAR -> {
                    val isTracking = event.cast(ActionTrackProgressBar::class.java).isTracking
                    isInterceptShow = isTracking
                    if (isTracking) {
                        hide()
                    }else {
                        show()
                    }
                }
                SceneEvent.Action.COMMENT_VISIBLE -> {
                    val visible = event.cast(ActionCommentVisible::class.java).visible
                    isInterceptShow = visible
                    if (!visible) {
                        show()
                    }else {
                        hide()
                    }
                }
            }
        }
    }
}