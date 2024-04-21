package com.benyq.tikbili.player.source

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 *
 * @author benyq
 * @date 4/8/2024
 *
 */
@Parcelize
data class MediaSource(
    private val tag: String,
    val url: String,
    val coverUrl: String,
    val width: Int,
    val height: Int,
    val stat: Stat,
    val poster: Poster,
): Parcelable {

    @IgnoredOnParcel
    val displayAspectRatio: Float = width.toFloat() / height

    fun getUniqueId(): String {
        return tag
    }

    @Parcelize
    data class Stat(
        //点赞
        val like: String = "",
        val coin: String = "",
        val reply: String = "",
        //收藏
        val favorite: String = "",
        //分享
        val share: String = "",
    ): Parcelable

    @Parcelize
    data class Poster(
        val avatar: String,
        val uid: Long,
        val nickName: String
    ): Parcelable
}