package com.benyq.tikbili.player.playback.layer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlayerEvent
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.playback.PlaybackEvent
import com.benyq.tikbili.player.playback.layer.base.AnimateLayer
import com.benyq.tikbli.player.R

/**
 *
 * @author benyq
 * @date 4/7/2024
 *
 */
class PauseLayer: AnimateLayer() {

    override val tag: String = "PauseLayer"

    override fun onCreateLayerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.view_pasue_layer, parent, false)
    }

    override fun onBindPlaybackController(controller: PlaybackController?) {
        controller?.addPlaybackListener(listener)
    }

    override fun onUnbindPlaybackController(controller: PlaybackController?) {
        controller?.removePlaybackListener(listener)
    }

    private val listener = object: EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when (event.code) {
                PlayerEvent.Action.PAUSE -> {
                    animateShow(false)
                }
                PlaybackEvent.Action.START_PLAYBACK, PlayerEvent.Action.START, PlayerEvent.Info.VIDEO_RENDERING_START -> {
                    animateDismiss()
                }
                PlayerEvent.Action.STOP, PlayerEvent.Action.RELEASE -> {
                    dismiss()
                }
            }
        }
    }
}