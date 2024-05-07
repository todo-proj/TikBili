package com.benyq.tikbili.player.download

import com.benyq.tikbili.player.source.MediaSource


/**
 *
 * @author benyq
 * @date 5/6/2024
 *
 */
interface IPreloadTask {

    companion object {
        const val EXTRA_PRELOAD_SIZE_IN_BYTES = "extra_preload_size_in_bytes"
        const val DEFAULT_PRELOAD_SIZE = (800 * 1024).toLong()
    }

    fun start()

    fun cancel()

    fun cacheKey(): String

    fun setDataSource(source: MediaSource)

    fun getDataSource(): MediaSource?

    fun setState(state: PreloadState)
    fun getState(): PreloadState

    fun isReleased(): Boolean

    fun isLoading(): Boolean

    fun isIdle(): Boolean


    interface Factory {
        fun create(): IPreloadTask
    }


    enum class PreloadState {
        IDLE, LOADING, ERROR, RELEASED
    }
}