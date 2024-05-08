package com.benyq.tikbili.scene.shortvideo.layer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.benyq.tikbili.R
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlaybackEvent
import com.benyq.tikbili.player.player.PlayerEvent
import com.benyq.tikbili.player.playback.layer.base.AnimateLayer
import com.benyq.tikbili.player.player.event.InfoProgressUpdate
import com.benyq.tikbili.player.utils.TimeUtils.time2String
import com.benyq.tikbili.scene.shortvideo.event.ActionTrackProgressBar

/**
 *
 * @author benyq
 * @date 4/10/2024
 *
 */
class ShortVideoProgressBarLayer: AnimateLayer() {

    override val tag: String = "ShortVideoProgressBarLayer"

    private var _seekBar: SeekBar? = null
    private var _text1: TextView? = null
    private var _text2: TextView? = null

    private var _touchSeeking = false
    private var _duration = 0L

    override fun onCreateLayerView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_short_video_progress_bar_layer, parent, false)
        _text1 = view.findViewById(R.id.text1)
        _text2 = view.findViewById(R.id.text2)
        _seekBar = view.findViewById(R.id.seekBar)
        _seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var mStartSeekProgress = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val percent = progress / seekBar.max.toFloat()
                val currentPosition: Long = (percent * _duration).toInt().toLong()
                _text1?.text = time2String(currentPosition)
                _text2?.text = time2String(_duration)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (_touchSeeking) return
                _touchSeeking = true
                mStartSeekProgress = seekBar.progress
                val startSeekPercent = mStartSeekProgress / seekBar.max.toFloat()
                val startSeekPosition: Long = (startSeekPercent * _duration).toLong()
                onManualStartSeek()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (!_touchSeeking) return
                _touchSeeking = false
                val startSeekPercent = mStartSeekProgress / seekBar.max.toFloat()
                val currentPercent = seekBar.progress / seekBar.max.toFloat()
                val startSeekPosition: Long = (startSeekPercent * _duration).toLong()
                val currentPosition: Long = (currentPercent * _duration).toLong()

                onManualStopSeek()
                val player = player() ?: return
                if (player.isInPlaybackState()) {
                    if (player.isCompleted()) {
                        player.start()
                        player.seekTo(currentPosition)
                    }else {
                        player.seekTo(currentPosition)
                    }
                }
            }
        })
        view.alpha = 0f
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


    private fun onManualStartSeek() {
        getView()?.alpha = 1f
        controller()?.dispatcher()?.obtain(ActionTrackProgressBar::class.java)?.init(true)?.dispatch()
    }

    private fun onManualStopSeek() {
        getView()?.alpha = 0f
        controller()?.dispatcher()?.obtain(ActionTrackProgressBar::class.java)?.init(false)?.dispatch()
    }

    private fun syncProgress() {
        val player = player() ?: return
        if (player.isInPlaybackState()) {
            setProgress(player.getCurrentPosition(), player.getDuration(), player.getBufferPercent())
        }
    }


    private fun setProgress(currentPosition: Long, duration: Long, bufferPercent: Int) {
        if (duration > 0) {
            setDuration(duration)
        }
        if (currentPosition > 0) {
            setCurrentPosition(currentPosition)
        }
        if (bufferPercent > 0) {
            setCachePercent(bufferPercent)
        }
    }

    private fun setDuration(duration: Long) {
        _duration = duration
        _seekBar?.max = (_duration / 1000).toInt().coerceAtLeast(100)
        _text2?.text = time2String(_duration)
    }

    private fun setCurrentPosition(currentPosition: Long) {
        if (!_touchSeeking) {
            _seekBar?.let {
                val progress: Int = (currentPosition / _duration.toFloat() * it.max).toInt()
                it.progress = progress
            }
        }
    }

    private fun setCachePercent(cachePercent: Int) {
        _seekBar?.let {
            it.secondaryProgress = (cachePercent * (it.max / 100f)).toInt()
        }
    }

    private val listener = object: EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when(event.code) {
                PlaybackEvent.State.BIND_PLAYER -> {
                    show()
                    getView()?.alpha = 0f
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