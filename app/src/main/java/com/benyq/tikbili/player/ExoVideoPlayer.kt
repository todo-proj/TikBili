package com.benyq.tikbili.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.TextureView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.video.VideoSize

/**
 *
 * @author benyq
 * @date 2023/5/26
 *
 */
class ExoVideoPlayer(private val context: Context, private val playWhenReady: Boolean) {

    companion object {
        private const val TAG = "ExoVideoPlayer"
    }

    //region 视频处理
    /*视频播放器*/
    private var mSimpleExoPlayer: SimpleExoPlayer? = null
    private var mOnVideoPlayListener: OnVideoPlayListener? = null

    var isVideoPlaying = false
        private set

    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var updateProgressAction: Runnable

    private val mMediaEventListener = object : Player.Listener {

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        }

        override fun onPlaybackStateChanged(state: Int) {
            mOnVideoPlayListener?.onPlaybackStateChanged(state)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            isVideoPlaying = isPlaying
            mOnVideoPlayListener?.onPlayingChanged(isPlaying)
            if (isPlaying) {
                mainHandler.post(updateProgressAction)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            val message: String = when (error.errorCode) {
                ExoPlaybackException.TYPE_SOURCE -> "数据源异常"
                ExoPlaybackException.TYPE_RENDERER -> "解码异常"
                ExoPlaybackException.TYPE_UNEXPECTED -> "其他异常"
                else -> "其他异常"
            }
            mOnVideoPlayListener?.onError(message)
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            mOnVideoPlayListener?.onVideoSizeChanged(videoSize.width, videoSize.height)
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            Log.d(TAG, "onTimelineChanged: $timeline.dur")
        }
    }

    fun create() {
        createMediaPlayer(context, playWhenReady)
        updateProgressAction = object: Runnable {
            override fun run() {
                val position = mSimpleExoPlayer?.currentPosition ?: 0L
                val bufferPosition = mSimpleExoPlayer?.contentBufferedPosition ?: 0L
                val duration = mSimpleExoPlayer?.duration ?: 0L
                mOnVideoPlayListener?.onProgressUpdate(position, bufferPosition)
                mainHandler.postDelayed(this, 500)
            }
        }
    }

    fun playVideo(url: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .build()
        val mediaSource: ProgressiveMediaSource =
            ProgressiveMediaSource.Factory(MediaCacheFactory.getCacheFactory(context.applicationContext))
                .createMediaSource(mediaItem)
        mSimpleExoPlayer?.setMediaSource(mediaSource)
        mSimpleExoPlayer?.prepare()
    }

    fun setRenderView(textureView: TextureView) {
        mSimpleExoPlayer?.setVideoTextureView(textureView)
    }

    fun setVideoPlayListener(listener: OnVideoPlayListener) {
        mOnVideoPlayListener = listener
    }

    fun getDuration(): Long {
        return mSimpleExoPlayer?.duration ?: 0
    }

    fun getVideoSize(): VideoSize? {
        return mSimpleExoPlayer?.videoSize
    }

    fun getPlayState(): Int {
        return mSimpleExoPlayer?.playbackState ?: Player.STATE_IDLE
    }

    fun seekTo(progress: Long) {
        mSimpleExoPlayer?.seekTo(progress)
    }

    fun pauseVideo() {
        mSimpleExoPlayer?.pause()
    }

    fun startVideo() {
        mSimpleExoPlayer?.play()
    }

    fun stopVideo() {
        mSimpleExoPlayer?.pause()
        mSimpleExoPlayer?.seekTo(0)
    }

    fun release() {
        mSimpleExoPlayer?.stop()
        mSimpleExoPlayer?.release()
    }

    /**
     * 初始化播放器
     */
    private fun createMediaPlayer(context: Context, playWhenReady: Boolean) {
        val simplePlayer = SimpleExoPlayer.Builder(context)
            .build()
        simplePlayer.addListener(mMediaEventListener)
        simplePlayer.playWhenReady = playWhenReady
        simplePlayer.repeatMode = Player.REPEAT_MODE_ALL
        mSimpleExoPlayer = simplePlayer
    }


    interface OnVideoPlayListener {

        fun onPlayingChanged(isPlaying: Boolean){}

        fun onPlaybackStateChanged(state: Int){}

        /**
         * 播放出错
         * @param error String 错误信息
         */
        fun onError(error: String){}

        fun onVideoSizeChanged(width: Int, height: Int){}

        fun onProgressUpdate(position: Long, bufferedPosition: Long){}
    }
}
