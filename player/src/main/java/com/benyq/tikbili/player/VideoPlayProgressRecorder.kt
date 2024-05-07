package com.benyq.tikbili.player

import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 4/10/2024
 *
 */
object VideoPlayProgressRecorder {

    private val progressMap = mutableMapOf<String, Long>()

    fun record(mediaSource: MediaSource, progress: Long) {
        progressMap[mediaSource.getUniqueId()] = progress
    }

    fun getProgress(mediaSource: MediaSource): Long {
        return progressMap[mediaSource.getUniqueId()] ?: 0
    }

    fun removeProgress(mediaSource: MediaSource) {
        progressMap.remove(mediaSource.getUniqueId())
    }
}