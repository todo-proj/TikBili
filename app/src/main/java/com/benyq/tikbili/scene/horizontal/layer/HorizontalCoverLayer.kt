package com.benyq.tikbili.scene.horizontal.layer

import android.graphics.Bitmap
import android.view.Surface
import android.widget.ImageView
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.player.PlayerEvent
import com.benyq.tikbili.player.playback.VideoView
import com.benyq.tikbili.player.playback.layer.CoverLayer
import com.benyq.tikbili.player.player.event.StatePlaying
import com.bumptech.glide.Glide

/**
 *
 * @author benyq
 * @date 4/11/2024
 *
 */
class HorizontalCoverLayer(private val currentFrame: Bitmap?): CoverLayer() {

    override fun onBindPlaybackController(controller: PlaybackController?) {
        controller?.addPlaybackListener(listener)
    }

    override fun onUnbindPlaybackController(controller: PlaybackController?) {
        controller?.removePlaybackListener(listener)
    }

    override fun onBindVideoView(videoView: VideoView) {
        show()
    }


    override fun show() {
        if (currentFrame == null) return
        super.show()
    }

    override fun load() {
        val imageView = getView() as? ImageView ?: return
        val activity = activity() ?: return
        if (!activity.isDestroyed) {
            Glide.with(imageView.context).load(currentFrame!!).listener(glideListener).into(imageView)
        }
    }

    private val listener = object : EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when(event.code) {
                PlayerEvent.State.PLAYING -> {
                    val isPlaying = event.cast(StatePlaying::class.java).isPlaying
                    if (isPlaying) {
                        dismiss()
                        //remove self in order to mainly release bitmap
                        videoView()?.layerHost()?.removeLayer(this@HorizontalCoverLayer)
                    }
                }
            }
        }
    }
}