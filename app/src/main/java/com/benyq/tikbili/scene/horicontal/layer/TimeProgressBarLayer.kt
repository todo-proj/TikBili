package com.benyq.tikbili.scene.horicontal.layer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.benyq.tikbili.R
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlaybackEvent
import com.benyq.tikbili.player.playback.PlayerEvent
import com.benyq.tikbili.player.playback.layer.base.AnimateLayer
import com.benyq.tikbili.player.player.event.InfoProgressUpdate
import com.benyq.tikbili.player.utils.TimeUtils
import com.benyq.tikbili.player.widget.MediaSeekBar

/**
 *
 * @author benyq
 * @date 4/26/2024
 *
 */
class TimeProgressBarLayer: AnimateLayer() {
    override val tag: String = "TimeProgressBarLayer"
    private var text1: TextView? = null
    private var text2: TextView? = null
    private var tvSpeed: TextView? = null
    private var mediaSeekbar: MediaSeekBar? = null

    override fun onCreateLayerView(parent: ViewGroup): View? {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_time_progress_bar_layer, parent, false)
        text1 = view.findViewById(R.id.text1)
        text2 = view.findViewById(R.id.text2)
        tvSpeed = view.findViewById(R.id.tv_speed)
        mediaSeekbar = view.findViewById(R.id.seek_bar)
        mediaSeekbar?.setTextVisibility(false)
        mediaSeekbar?.setOnSeekListener(object : MediaSeekBar.OnUserSeekListener {
            override fun onUserSeekStart(startPosition: Long) {
                showControllerLayers()
            }
            override fun onUserSeekPeeking(peekPosition: Long) {
                showControllerLayers()
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
                showControllerLayers()
            }
        })
        tvSpeed?.setOnClickListener {
            val layer = layerHost()?.findLayer(SpeedSelectDialogLayer::class.java)
            layer?.animateShow(false)
        }
        return view
    }

    override fun show() {
        super.show()
        sync()
    }

    override fun onBindPlaybackController(controller: PlaybackController?) {
        controller?.addPlaybackListener(listener)
    }

    override fun onUnbindPlaybackController(controller: PlaybackController?) {
        controller?.removePlaybackListener(listener)
    }

    private val listener = object : EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when(event.code) {
                PlaybackEvent.State.BIND_PLAYER -> {
                    syncProgress()
                }
                PlayerEvent.Info.PROGRESS_UPDATE -> {
                    if (event is InfoProgressUpdate) {
                        setProgress(event.currentPosition, event.duration, event.buffer.toInt())
                    }
                }
            }
        }
    }

    private fun showControllerLayers() {
        layerHost()?.findLayer(GestureLayer::class.java)?.showController()
    }

    private fun sync() {
        syncProgress()
        syncSpeed()
    }

    private fun syncProgress() {
        val player = player() ?: return
        if (player.isInPlaybackState()) {
            setProgress(
                player.getCurrentPosition(),
                player.getDuration(),
                player.getBufferPercent()
            )
        }
    }


    @SuppressLint("SetTextI18n")
    private fun syncSpeed() {
        val player = player()
        if (player == null) {
            tvSpeed?.text = "Speed"
            return
        }
        val speed = player.getSpeed()
        if (speed != 1f) {
            tvSpeed?.text = "${speed}X"
        }else {
            tvSpeed?.text = "Speed"
        }
    }

    private fun setProgress(currentPosition: Long, duration: Long, bufferPercent: Int) {
        mediaSeekbar?.let {
            if (duration >= 0) {
                it.setDuration(duration)
            }
            if (currentPosition >= 0) {
                it.setCurrentPosition(currentPosition)
            }
            if (bufferPercent >= 0) {
                it.setCachePercent(bufferPercent)
            }
        }

        if (currentPosition >= 0) {
            text1?.text = TimeUtils.time2String(currentPosition)
        }
        if (duration >= 0) {
            text2?.text = TimeUtils.time2String(duration)
        }
    }
}