package com.benyq.tikbili.player.source

import java.io.Serializable

/**
 *
 * @author benyq
 * @date 4/8/2024
 *
 */
data class MediaSource(
    private val tag: String,
    val url: String,
    val coverUrl: String,
    val width: Int,
    val height: Int,
    val duration: Long,
    val byteSize: Long
) : ExtraObject(), Serializable {

    val displayAspectRatio: Float = width.toFloat() / height

    fun getUniqueId(): String {
        return tag
    }

}