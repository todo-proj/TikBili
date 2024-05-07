package com.benyq.tikbili.player.download

import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 5/7/2024
 *
 */
interface CacheLoader {

    object Default {
        private var sInstance: CacheLoader? = null
        @Synchronized
        fun set(loader: CacheLoader) {
            sInstance = loader
        }

        @Synchronized
        fun get(): CacheLoader? {
            return sInstance
        }
    }

    fun createTask(): IPreloadTask

    fun preload(source: MediaSource)

    fun stopTask(source: MediaSource)

    fun stopTask(mediaId: String)

    fun stopAllTask()

    fun clearCache()


}