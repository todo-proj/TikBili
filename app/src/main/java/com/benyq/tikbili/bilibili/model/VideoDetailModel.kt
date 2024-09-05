package com.benyq.tikbili.bilibili.model


import com.google.gson.annotations.SerializedName

data class VideoDetailModel(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cid")
    val cid: Long,
    @SerializedName("copyright")
    val copyright: Int,
    @SerializedName("ctime")
    val ctime: Int,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("desc_v2")
    val descV2: List<DescV2>,
    @SerializedName("dimension")
    val dimension: Dimension,
    @SerializedName("disable_show_up_info")
    val disableShowUpInfo: Boolean,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("dynamic")
    val `dynamic`: String,
    @SerializedName("enable_vt")
    val enableVt: Int,
    @SerializedName("is_chargeable_season")
    val isChargeableSeason: Boolean,
    @SerializedName("is_season_display")
    val isSeasonDisplay: Boolean,
    @SerializedName("is_story")
    val isStory: Boolean,
    @SerializedName("is_upower_exclusive")
    val isUpowerExclusive: Boolean,
    @SerializedName("is_upower_play")
    val isUpowerPlay: Boolean,
    @SerializedName("like_icon")
    val likeIcon: String,
    @SerializedName("mission_id")
    val missionId: Int,
    @SerializedName("need_jump_bv")
    val needJumpBv: Boolean,
    @SerializedName("no_cache")
    val noCache: Boolean,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("pages")
    val pages: List<Page>,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("premiere")
    val premiere: Any,
    @SerializedName("pubdate")
    val pubdate: Int,
    @SerializedName("season_id")
    val seasonId: Int,
    @SerializedName("stat")
    val stat: Stat,
    @SerializedName("state")
    val state: Int,
    @SerializedName("teenage_mode")
    val teenageMode: Int,
    @SerializedName("tid")
    val tid: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("tname")
    val tname: String,
    @SerializedName("ugc_season")
    val ugcSeason: UgcSeason,
    @SerializedName("videos")
    val videos: Int
) {
    data class DescV2(
        @SerializedName("biz_id")
        val bizId: Int,
        @SerializedName("raw_text")
        val rawText: String,
        @SerializedName("type")
        val type: Int
    )

    data class Dimension(
        @SerializedName("height")
        val height: Int,
        @SerializedName("rotate")
        val rotate: Int,
        @SerializedName("width")
        val width: Int
    )

    data class Owner(
        @SerializedName("face")
        val face: String,
        @SerializedName("mid")
        val mid: Long,
        @SerializedName("name")
        val name: String
    )

    data class Page(
        @SerializedName("cid")
        val cid: Long,
        @SerializedName("dimension")
        val dimension: Dimension,
        @SerializedName("duration")
        val duration: Int,
        @SerializedName("first_frame")
        val firstFrame: String,
        @SerializedName("from")
        val from: String,
        @SerializedName("page")
        val page: Int,
        @SerializedName("part")
        val part: String,
        @SerializedName("vid")
        val vid: String,
        @SerializedName("weblink")
        val weblink: String
    ) {
        data class Dimension(
            @SerializedName("height")
            val height: Int,
            @SerializedName("rotate")
            val rotate: Int,
            @SerializedName("width")
            val width: Int
        )
    }

    data class Stat(
        @SerializedName("aid")
        val aid: Long,
        @SerializedName("argue_msg")
        val argueMsg: String,
        @SerializedName("coin")
        val coin: Int,
        @SerializedName("danmaku")
        val danmaku: Int,
        @SerializedName("dislike")
        val dislike: Int,
        @SerializedName("evaluation")
        val evaluation: String,
        @SerializedName("favorite")
        val favorite: Int,
        @SerializedName("his_rank")
        val hisRank: Int,
        @SerializedName("like")
        val like: Int,
        @SerializedName("now_rank")
        val nowRank: Int,
        @SerializedName("reply")
        val reply: Int,
        @SerializedName("share")
        val share: Int,
        @SerializedName("view")
        val view: Int,
        @SerializedName("vt")
        val vt: Int
    )

    data class UgcSeason(
        @SerializedName("attribute")
        val attribute: Int,
        @SerializedName("cover")
        val cover: String,
        @SerializedName("enable_vt")
        val enableVt: Int,
        @SerializedName("ep_count")
        val epCount: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("intro")
        val intro: String,
        @SerializedName("is_pay_season")
        val isPaySeason: Boolean,
        @SerializedName("mid")
        val mid: Long,
        @SerializedName("season_type")
        val seasonType: Int,
        @SerializedName("sections")
        val sections: List<Section>,
        @SerializedName("sign_state")
        val signState: Int,
        @SerializedName("stat")
        val stat: Stat,
        @SerializedName("title")
        val title: String
    ) {
        data class Section(
            @SerializedName("episodes")
            val episodes: List<Episode>,
            @SerializedName("id")
            val id: Int,
            @SerializedName("season_id")
            val seasonId: Int,
            @SerializedName("title")
            val title: String,
            @SerializedName("type")
            val type: Int
        ) {
            data class Episode(
                @SerializedName("aid")
                val aid: Long,
                @SerializedName("arc")
                val arc: Arc,
                @SerializedName("attribute")
                val attribute: Int,
                @SerializedName("bvid")
                val bvid: String,
                @SerializedName("cid")
                val cid: Long,
                @SerializedName("id")
                val id: Int,
                @SerializedName("page")
                val page: Page,
                @SerializedName("season_id")
                val seasonId: Int,
                @SerializedName("section_id")
                val sectionId: Int,
                @SerializedName("title")
                val title: String
            ) {
                data class Arc(
                    @SerializedName("aid")
                    val aid: Long,
                    @SerializedName("author")
                    val author: Author,
                    @SerializedName("copyright")
                    val copyright: Int,
                    @SerializedName("ctime")
                    val ctime: Int,
                    @SerializedName("desc")
                    val desc: String,
                    @SerializedName("desc_v2")
                    val descV2: Any,
                    @SerializedName("dimension")
                    val dimension: Dimension,
                    @SerializedName("duration")
                    val duration: Int,
                    @SerializedName("dynamic")
                    val `dynamic`: String,
                    @SerializedName("enable_vt")
                    val enableVt: Int,
                    @SerializedName("is_blooper")
                    val isBlooper: Boolean,
                    @SerializedName("is_chargeable_season")
                    val isChargeableSeason: Boolean,
                    @SerializedName("pic")
                    val pic: String,
                    @SerializedName("pubdate")
                    val pubdate: Int,
                    @SerializedName("stat")
                    val stat: Stat,
                    @SerializedName("state")
                    val state: Int,
                    @SerializedName("title")
                    val title: String,
                    @SerializedName("type_id")
                    val typeId: Int,
                    @SerializedName("type_name")
                    val typeName: String,
                    @SerializedName("videos")
                    val videos: Int
                ) {
                    data class Author(
                        @SerializedName("face")
                        val face: String,
                        @SerializedName("mid")
                        val mid: Long,
                        @SerializedName("name")
                        val name: String
                    )

                    data class Dimension(
                        @SerializedName("height")
                        val height: Int,
                        @SerializedName("rotate")
                        val rotate: Int,
                        @SerializedName("width")
                        val width: Int
                    )

                    data class Stat(
                        @SerializedName("aid")
                        val aid: Long,
                        @SerializedName("argue_msg")
                        val argueMsg: String,
                        @SerializedName("coin")
                        val coin: Int,
                        @SerializedName("danmaku")
                        val danmaku: Int,
                        @SerializedName("dislike")
                        val dislike: Int,
                        @SerializedName("evaluation")
                        val evaluation: String,
                        @SerializedName("fav")
                        val fav: Int,
                        @SerializedName("his_rank")
                        val hisRank: Int,
                        @SerializedName("like")
                        val like: Int,
                        @SerializedName("now_rank")
                        val nowRank: Int,
                        @SerializedName("reply")
                        val reply: Int,
                        @SerializedName("share")
                        val share: Int,
                        @SerializedName("view")
                        val view: Int,
                        @SerializedName("vt")
                        val vt: Int,
                        @SerializedName("vv")
                        val vv: Int
                    )
                }

                data class Page(
                    @SerializedName("cid")
                    val cid: Long,
                    @SerializedName("dimension")
                    val dimension: Dimension,
                    @SerializedName("duration")
                    val duration: Int,
                    @SerializedName("from")
                    val from: String,
                    @SerializedName("page")
                    val page: Int,
                    @SerializedName("part")
                    val part: String,
                    @SerializedName("vid")
                    val vid: String,
                    @SerializedName("weblink")
                    val weblink: String
                ) {
                    data class Dimension(
                        @SerializedName("height")
                        val height: Int,
                        @SerializedName("rotate")
                        val rotate: Int,
                        @SerializedName("width")
                        val width: Int
                    )
                }
            }
        }

        data class Stat(
            @SerializedName("coin")
            val coin: Int,
            @SerializedName("danmaku")
            val danmaku: Int,
            @SerializedName("fav")
            val fav: Int,
            @SerializedName("his_rank")
            val hisRank: Int,
            @SerializedName("like")
            val like: Int,
            @SerializedName("now_rank")
            val nowRank: Int,
            @SerializedName("reply")
            val reply: Int,
            @SerializedName("season_id")
            val seasonId: Int,
            @SerializedName("share")
            val share: Int,
            @SerializedName("view")
            val view: Int,
            @SerializedName("vt")
            val vt: Int,
            @SerializedName("vv")
            val vv: Int
        )
    }
}