package com.benyq.tikbili.bilibili.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RecommendVideoData(val item: List<RecommendVideoModel>)

@Parcelize
data class RecommendVideoModel(
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cid")
    val cid: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("enable_vt")
    val enableVt: Int,
    @SerializedName("goto")
    val goto: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_followed")
    val isFollowed: Int,
    @SerializedName("is_stock")
    val isStock: Int,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("pos")
    val pos: Int,
    @SerializedName("pubdate")
    val pubdate: Int,
    @SerializedName("show_info")
    val showInfo: Int,
    @SerializedName("stat")
    val stat: Stat,
    @SerializedName("title")
    val title: String,
    @SerializedName("track_id")
    val trackId: String,
    @SerializedName("uri")
    val uri: String
): Parcelable {

    @Parcelize
    data class Owner(
        @SerializedName("face")
        val face: String,
        @SerializedName("mid")
        val mid: Long,
        @SerializedName("name")
        val name: String
    ): Parcelable

    @Parcelize
    data class Stat(
        @SerializedName("danmaku")
        val danmaku: Int,
        @SerializedName("like")
        val like: Int,
        @SerializedName("view")
        val view: Int,
        @SerializedName("vt")
        val vt: Int
    ): Parcelable
}