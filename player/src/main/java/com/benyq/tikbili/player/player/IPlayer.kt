package com.benyq.tikbili.player.player

import android.view.Surface
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbili.player.dispather.EventDispatcher

/**
 *
 * @author benyq
 * @date 4/8/2024
 *
 */
 abstract class IPlayer {

    abstract fun setDataSource(mediaSource: MediaSource)
    abstract fun getDataSource(): MediaSource?

    abstract fun setStartWhenPrepared(startWhenPrepared: Boolean)

    abstract fun prepare()

    abstract fun start()

    abstract fun pause()

    abstract fun stop()

    abstract fun release()

    abstract fun setSurface(surface: Surface?)

    abstract fun getVideoWidth(): Int

    abstract fun getVideoHeight(): Int

    abstract fun getDuration(): Long
    abstract fun getCurrentPosition(): Long
    abstract fun getBufferPercent(): Int
    abstract fun seekTo(progress: Long)

    abstract fun isReleased(): Boolean

    abstract fun isIDLE(): Boolean
    abstract fun isCompleted(): Boolean
    abstract fun isPlaying(): Boolean
    abstract fun isPaused(): Boolean
    abstract fun isLooping(): Boolean
    abstract fun setSpeed(speed: Float)

    abstract fun isInPlaybackState(): Boolean

    abstract fun getState(): PlayState
    abstract fun addPlayerListener(listener: EventDispatcher.EventListener)

    abstract fun removePlayerListener(listener: EventDispatcher.EventListener)

    protected abstract fun setState(state: PlayState)

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