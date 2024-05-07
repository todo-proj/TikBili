package com.benyq.tikbili.player.player

import android.view.Surface
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 4/8/2024
 *
 */
 interface IPlayer {

     fun getDataSource(): MediaSource?

     fun setStartWhenPrepared(startWhenPrepared: Boolean)

    fun setStartTime(startTime: Long)
     fun prepare(mediaSource: MediaSource)

     fun start()

     fun pause()

     fun stop()

     fun release()

     fun setSurface(surface: Surface?)

     fun getVideoWidth(): Int

     fun getVideoHeight(): Int

     fun getDuration(): Long
     fun getCurrentPosition(): Long
     fun getBufferPercent(): Int
     fun seekTo(progress: Long)

     fun isReleased(): Boolean

     fun isIDLE(): Boolean
     fun isCompleted(): Boolean
     fun isError(): Boolean
     fun isPreparing(): Boolean
     fun isPlaying(): Boolean
     fun isPaused(): Boolean
     fun isLooping(): Boolean
     fun setLooping(looping: Boolean)
     fun setSpeed(speed: Float)
     fun getSpeed(): Float

     fun setVolume(volume: Float)
     fun getVolume(): Float

     fun isInPlaybackState(): Boolean

     fun getState(): PlayState
     fun addPlayerListener(listener: EventDispatcher.EventListener)

     fun removePlayerListener(listener: EventDispatcher.EventListener)

     fun setState(state: PlayState)

    interface Factory {
        object Default {
            private lateinit var sInstance: Factory
            @Synchronized
            fun set(factory: Factory) {
                sInstance = factory
            }

            @Synchronized
            fun get(): Factory {
                return sInstance
            }
        }

        fun create(source: MediaSource): IPlayer
    }
    enum class PlayState {
        STATE_IDLE,
        STATE_PREPARING,
        STATE_PREPARED,
        STATE_STARTED,
        STATE_PAUSED,
        STATE_COMPLETED,
        STATE_ERROR,
        STATE_STOPPED,
        STATE_RELEASED
    }
}