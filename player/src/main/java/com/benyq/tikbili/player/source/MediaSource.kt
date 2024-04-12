package com.benyq.tikbili.player.source

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
    val stat: Stat,
    val poster: Poster,
) {

    val displayAspectRatio: Float = width.toFloat() / height

    fun getUniqueId(): String {
        return tag
    }

    data class Stat(
        //点赞
        val like: String = "",
        val coin: String = "",
        val reply: String = "",
        //收藏
        val favorite: String = "",
        //分享
        val share: String = "",
    )

    data class Poster(
        val avatar: String,
        val uid: Long,
        val nickName: String
    )
}