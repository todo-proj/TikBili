package com.benyq.tikbili.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Surface
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.player.player.IPlayer.PlayState
import com.benyq.tikbili.player.player.PlayerAdapter
import com.benyq.tikbili.player.player.event.ActionPause
import com.benyq.tikbili.player.player.event.ActionPrepare
import com.benyq.tikbili.player.player.event.ActionSetSurface
import com.benyq.tikbili.player.player.event.ActionStart
import com.benyq.tikbili.player.player.event.InfoProgressUpdate
import com.benyq.tikbili.player.player.event.InfoVideoRenderingStart
import com.benyq.tikbili.player.player.event.StateCompleted
import com.benyq.tikbili.player.player.event.StatePlaying
import com.benyq.tikbili.player.source.MediaSource
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.video.VideoSize

/**
 *
 * @author benyq
 * @date 2023/5/26
 *
 */
class ExoVideoPlayer(private val context: Context): PlayerAdapter() {

    companion object {
        private const val TAG = "ExoVideoPlayer"
    }

    //region 视频处理
    /*视频播放器*/
    private lateinit var mSimpleExoPlayer: SimpleExoPlayer
    private var mMediaStore: MediaSource? = null

    private var isVideoPlaying = false
        private set
    private var isFirstFrameRendered = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var updateProgressAction: Runnable

    private var startTime: Long = -1L
    private var _isLooping = false

    private val mMediaEventListener = object : Player.Listener {

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            L.d(this@ExoVideoPlayer, "onPlayWhenReadyChanged", playWhenReady, reason)
        }

        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_READY) {
                if (isPreparing()) {
                    setState(PlayState.STATE_PREPARED)
                    if (startTime != -1L) {
                        mSimpleExoPlayer.seekTo(startTime)
                    }
                }
            }
            if (state == Player.STATE_ENDED) {
                _dispatcher.obtain(StateCompleted::class.java).dispatch()
                clearProgress()
                setState(PlayState.STATE_COMPLETED)
                if (isLooping()) {
                    start()
                }
            }
            L.d(this@ExoVideoPlayer, "onPlaybackStateChanged", state)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            L.d(this@ExoVideoPlayer, "onIsPlayingChanged", isPlaying)
            isVideoPlaying = isPlaying
            if (isPlaying) {
                mainHandler.post(updateProgressAction)
                setState(PlayState.STATE_STARTED)
            }else {
                mainHandler.removeCallbacks(updateProgressAction)
            }
            _dispatcher.obtain(StatePlaying::class.java).init(isPlaying).dispatch()
        }

        override fun onPlayerError(error: PlaybackException) {
            val message: String = when (error.errorCode) {
                ExoPlaybackException.TYPE_SOURCE -> "数据源异常"
                ExoPlaybackException.TYPE_RENDERER -> "解码异常"
                ExoPlaybackException.TYPE_UNEXPECTED -> "其他异常"
                else -> "其他异常"
            }
            L.e(this@ExoVideoPlayer, "onPlayerError", message)
            setState(PlayState.STATE_ERROR)
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
        }

        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()
            if (!isFirstFrameRendered) {
                isFirstFrameRendered = true
                _dispatcher.obtain(InfoVideoRenderingStart::class.java).dispatch()
                L.d(this@ExoVideoPlayer, "onRenderedFirstFrame")
            }
        }
    }

    fun create() {
        createMediaPlayer(context)
        updateProgressAction = object: Runnable {
            override fun run() {
                val position = mSimpleExoPlayer.currentPosition
                val bufferPosition = mSimpleExoPlayer.contentBufferedPosition
                val duration = mSimpleExoPlayer.duration
                _dispatcher.obtain(InfoProgressUpdate::class.java).init(position, duration, bufferPosition).dispatch()
                mainHandler.postDelayed(this, 50)

                recordProgress()
            }
        }
    }

    private fun playVideo(key: String, url: String) {
        val mediaItem = MediaItem.Builder()
            .setCustomCacheKey(key)
            .setUri(url)
            .build()
        val mediaSource: ProgressiveMediaSource =
            ProgressiveMediaSource.Factory(MediaCacheFactory.getCacheFactory(context.applicationContext))
                .createMediaSource(mediaItem)
        mSimpleExoPlayer.setMediaSource(mediaSource)
        mSimpleExoPlayer.prepare()
    }

    override fun getDuration(): Long {
        return mSimpleExoPlayer.duration
    }

    override fun getCurrentPosition(): Long {
        return mSimpleExoPlayer.currentPosition
    }

    override fun getBufferPercent(): Int {
        return (mSimpleExoPlayer.contentBufferedPosition / mSimpleExoPlayer.duration).toInt()
    }

    override fun seekTo(progress: Long) {
        mSimpleExoPlayer.seekTo(progress)
    }

    override fun isLooping(): Boolean {
        return _isLooping
    }

    override fun setLooping(looping: Boolean) {
        if (looping == isLooping()) return
        _isLooping = looping
    }

    override fun setSpeed(speed: Float) {
        mSimpleExoPlayer.setPlaybackSpeed(speed)
    }

    override fun getSpeed(): Float {
        return mSimpleExoPlayer.playbackParameters.speed
    }

    override fun setVolume(volume: Float) {
        mSimpleExoPlayer.volume = volume
    }

    override fun getVolume(): Float {
        return mSimpleExoPlayer.volume
    }

    override fun getDataSource(): MediaSource? {
        return mMediaStore
    }

    override fun setStartWhenPrepared(startWhenPrepared: Boolean) {
        mSimpleExoPlayer.playWhenReady = startWhenPrepared
    }

    override fun setStartTime(startTime: Long) {
        this.startTime = startTime
        L.d(this@ExoVideoPlayer, "setStartTime", startTime)
    }

    override fun prepare(mediaSource: MediaSource) {
        if (checkIsRelease("prepare")) return
        _dispatcher.obtain(ActionPrepare::class.java).init(mediaSource).dispatch()
        mMediaStore = mediaSource
        // set start time
        setStartTime(mediaSource)
        playVideo(mediaSource.getUniqueId(), mediaSource.url)
        setState(PlayState.STATE_PREPARING)
    }

    override fun start() {
        if (checkIsRelease("start")) return
        _dispatcher.obtain(ActionStart::class.java).dispatch()
        mSimpleExoPlayer.play()
        if (isCompleted()) {
            seekTo(0)
        }
        setState(PlayState.STATE_STARTED)
    }

    override fun pause() {
        if (checkIsRelease("pause")) return
        _dispatcher.obtain(ActionPause::class.java).dispatch()
        recordProgress()
        mSimpleExoPlayer.pause()
        setState(PlayState.STATE_PAUSED)
    }

    override fun stop() {
        if (checkIsRelease("stop")) return
        recordProgress()
        mSimpleExoPlayer.pause()
        setState(PlayState.STATE_STOPPED)
    }

    override fun release() {
        if (checkIsRelease("release")) return
        recordProgress()
        mSimpleExoPlayer.stop()
        mSimpleExoPlayer.release()
        setState(PlayState.STATE_RELEASED)
    }

    override fun setSurface(surface: Surface?) {
        surface?.let {
            _dispatcher.obtain(ActionSetSurface::class.java).init(surface).dispatch()
            mSimpleExoPlayer.setVideoSurface(it)
        }
    }

    override fun getVideoWidth(): Int {
        return mSimpleExoPlayer.videoSize.width
    }

    override fun getVideoHeight(): Int {
        return mSimpleExoPlayer.videoSize.height
    }

    /**
     * 初始化播放器
     */
    private fun createMediaPlayer(context: Context) {
        val simplePlayer = SimpleExoPlayer.Builder(context)
            .build()
        simplePlayer.addListener(mMediaEventListener)
        mSimpleExoPlayer = simplePlayer
    }

    private fun setStartTime(source: MediaSource) {
        if (startTime <= 0) {
            val progress = VideoPlayProgressRecorder.getProgress(source)
            if (progress > 0) {
                startTime = progress
            }
        }
    }

    private fun recordProgress() {
        val dataSource = getDataSource() ?: return
        if (isInPlaybackState() && !isCompleted() || isError()) {
            val progress = mSimpleExoPlayer.currentPosition
            val duration = mSimpleExoPlayer.duration
            if (progress > 1000 && duration > 0 && progress < duration - 1000) {
                VideoPlayProgressRecorder.record(dataSource, progress)
            }
        }
    }

    private fun clearProgress() {
        val mediaSource: MediaSource = getDataSource() ?: return
        VideoPlayProgressRecorder.removeProgress(mediaSource)
    }
}
