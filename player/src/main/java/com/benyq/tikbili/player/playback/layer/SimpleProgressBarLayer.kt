package com.benyq.tikbili.player.playback.layer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlaybackEvent
import com.benyq.tikbili.player.player.PlayerEvent
import com.benyq.tikbili.player.playback.layer.base.AnimateLayer
import com.benyq.tikbili.player.player.event.InfoProgressUpdate
import com.benyq.tikbili.player.widget.MediaSeekBar
import com.benyq.tikbli.player.R

/**
 *
 * @author benyq
 * @date 4/10/2024
 *
 */
class SimpleProgressBarLayer: AnimateLayer() {

    override val tag: String = "SimpleProgressBarLayer"

    private var _seekBar: MediaSeekBar? = null

    override fun onCreateLayerView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_simple_progress_bar_layer, parent, false)

        _seekBar = view.findViewById(R.id.mediaSeekBar)
        _seekBar?.setOnSeekListener(object : MediaSeekBar.OnUserSeekListener {
            override fun onUserSeekStart(startPosition: Long) {

            }
            override fun onUserSeekPeeking(peekPosition: Long) {

            }
            override fun onUserSeekStop(startPosition: Long, seekToPosition: Long) {
                val player = player() ?: return
                if (player.isInPlaybackState()) {
                    if (player.isCompleted()) {
                        player.start()
                        player.seekTo(seekToPosition)
                    }else {
                        player.seekTo(seekToPosition)
                    }
                }
            }
        })
        return view
    }

    override fun onBindPlaybackController(controller: PlaybackController?) {
        controller?.addPlaybackListener(listener)
    }

    override fun onUnbindPlaybackController(controller: PlaybackController?) {
        controller?.removePlaybackListener(listener)
        dismiss()
    }

    override fun show() {
        super.show()
        syncProgress()
    }

    private fun syncProgress() {
        val player = player() ?: return
        if (player.isInPlaybackState()) {
            setProgress(player.getCurrentPosition(), player.getDuration(), player.getBufferPercent())
        }
    }

    private fun setProgress(currentPosition: Long, duration: Long, bufferPercent: Int) {
        if (duration > 0) {
            _seekBar?.setDuration(duration)
        }
        if (currentPosition > 0) {
            _seekBar?.setCurrentPosition(currentPosition)
        }
        if (bufferPercent > 0) {
            _seekBar?.setCachePercent(bufferPercent)
        }
    }

    private val listener = object: EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when(event.code) {
                PlaybackEvent.State.BIND_PLAYER -> {
                    syncProgress()
                }
                PlayerEvent.Action.PAUSE -> {
                    animateDismiss()
                }
                PlayerEvent.Action.START -> {
                    animateShow(false)
                }
                PlayerEvent.State.STOPPED, PlayerEvent.State.ERROR, PlayerEvent.State.RELEASED -> {
                    dismiss()
                }
                PlayerEvent.Info.PROGRESS_UPDATE -> {
                    if (event is InfoProgressUpdate) {
                        setProgress(event.currentPosition, event.duration, -1)
                    }
                }
            }
        }
    }
}