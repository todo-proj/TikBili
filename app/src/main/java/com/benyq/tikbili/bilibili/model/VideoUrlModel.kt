package com.benyq.tikbili.bilibili.model


import com.google.gson.annotations.SerializedName

data class VideoUrlModel(
    @SerializedName("accept_description")
    val acceptDescription: List<String>,
    @SerializedName("accept_format")
    val acceptFormat: String,
    @SerializedName("accept_quality")
    val acceptQuality: List<Int>,
    @SerializedName("durl")
    val durl: List<Durl>,
    @SerializedName("format")
    val format: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("high_format")
    val highFormat: Any,
    @SerializedName("last_play_cid")
    val lastPlayCid: Int,
    @SerializedName("last_play_time")
    val lastPlayTime: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("quality")
    val quality: Int,
    @SerializedName("result")
    val result: String,
    @SerializedName("seek_param")
    val seekParam: String,
    @SerializedName("seek_type")
    val seekType: String,
    @SerializedName("support_formats")
    val supportFormats: List<SupportFormat>,
    @SerializedName("timelength")
    val timelength: Int,
    @SerializedName("video_codecid")
    val videoCodecid: Int
) {
    data class Durl(
        @SerializedName("ahead")
        val ahead: String,
        @SerializedName("backup_url")
        val backupUrl: List<String>,
        @SerializedName("length")
        val length: Int,
        @SerializedName("order")
        val order: Int,
        @SerializedName("size")
        val size: Int,
        @SerializedName("url")
        val url: String,
        @SerializedName("vhead")
        val vhead: String
    )

    data class SupportFormat(
        @SerializedName("codecs")
        val codecs: Any,
        @SerializedName("display_desc")
        val displayDesc: String,
        @SerializedName("format")
        val format: String,
        @SerializedName("new_description")
        val newDescription: String,
        @SerializedName("quality")
        val quality: Int,
        @SerializedName("superscript")
        val superscript: String
    )
}