package com.benyq.tikbili.scene.shortvideo.layer

import android.view.Surface
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlayerEvent
import com.benyq.tikbili.player.playback.layer.CoverLayer

/**
 *
 * @author benyq
 * @date 4/11/2024
 *
 */
class ShortVideoCoverLayer: CoverLayer() {


    override fun onBindPlaybackController(controller: PlaybackController?) {
        controller?.addPlaybackListener(listener)
    }

    override fun onUnbindPlaybackController(controller: PlaybackController?) {
        controller?.removePlaybackListener(listener)
    }

    override fun onSurfaceAvailable(surface: Surface, width: Int, height: Int) {
        show()
    }

    private val listener = object : EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when(event.code) {
                PlayerEvent.Info.VIDEO_RENDERING_START -> {
                    dismiss()
                }
            }
        }
    }
}