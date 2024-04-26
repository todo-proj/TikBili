package com.benyq.tikbili.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.player.player.event.ActionPause
import com.benyq.tikbili.player.player.event.ActionStart
import com.benyq.tikbili.player.player.event.InfoProgressUpdate
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbili.player.player.PlayerAdapter
import com.benyq.tikbili.player.player.event.ActionSetSurface
import com.benyq.tikbili.player.player.event.InfoVideoRenderingStart
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheWriter
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

    private val mMediaEventListener = object : Player.Listener {

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            L.d(this@ExoVideoPlayer, "onPlayWhenReadyChanged", playWhenReady, reason)
        }

        override fun onPlaybackStateChanged(state: Int) {
            L.d(this@ExoVideoPlayer, "onPlaybackStateChanged", state)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            isVideoPlaying = isPlaying
            if (isPlaying) {
                mainHandler.post(updateProgressAction)
                setState(PlayState.STATE_STARTED)
            }else {
                mainHandler.removeCallbacks(updateProgressAction)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            val message: String = when (error.errorCode) {
                ExoPlaybackException.TYPE_SOURCE -> "数据源异常"
                ExoPlaybackException.TYPE_RENDERER -> "解码异常"
                ExoPlaybackException.TYPE_UNEXPECTED -> "其他异常"
                else -> "其他异常"
            }
            L.e(this@ExoVideoPlayer, "onPlayerError", message)
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
                mainHandler.postDelayed(this, 30)
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
        return mSimpleExoPlayer.repeatMode == Player.REPEAT_MODE_ALL
    }

    override fun setSpeed(speed: Float) {
        mSimpleExoPlayer.setPlaybackSpeed(speed)
    }

    override fun getSpeed(): Float {
        return mSimpleExoPlayer.playbackParameters.speed
    }

    override fun setDataSource(mediaSource: MediaSource) {
        mMediaStore = mediaSource
    }

    override fun getDataSource(): MediaSource? {
        return mMediaStore
    }

    override fun setStartWhenPrepared(startWhenPrepared: Boolean) {
        mSimpleExoPlayer.playWhenReady = startWhenPrepared
    }

    override fun prepare() {
        mMediaStore?.let {
            playVideo(it.getUniqueId(), it.url)
        }
    }

    override fun start() {
        _dispatcher.obtain(ActionStart::class.java).dispatch()
        mSimpleExoPlayer.play()
        setState(PlayState.STATE_STARTED)
    }

    override fun pause() {
        _dispatcher.obtain(ActionPause::class.java).dispatch()
        mSimpleExoPlayer.pause()
        setState(PlayState.STATE_PAUSED)
    }

    override fun stop() {
        getDataSource()?.let {
            VideoPlayProgressRecorder.record(it, mSimpleExoPlayer.currentPosition)
        }
        mSimpleExoPlayer.pause()
        mSimpleExoPlayer.seekTo(0)
    }

    override fun release() {
        getDataSource()?.let {
            VideoPlayProgressRecorder.record(it, mSimpleExoPlayer.currentPosition)
        }
        mSimpleExoPlayer.stop()
        mSimpleExoPlayer.release()
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
        simplePlayer.repeatMode = Player.REPEAT_MODE_ALL
        mSimpleExoPlayer = simplePlayer
    }
}
