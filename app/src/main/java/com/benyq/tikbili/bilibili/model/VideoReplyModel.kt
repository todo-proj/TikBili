package com.benyq.tikbili.bilibili.model


import com.google.gson.annotations.SerializedName

data class VideoReplyModel(
    @SerializedName("assist")
    val assist: Int,
    @SerializedName("blacklist")
    val blacklist: Int,
    @SerializedName("config")
    val config: Config,
    @SerializedName("control")
    val control: Control,
    @SerializedName("folder")
    val folder: Folder,
    @SerializedName("hots")
    val hots: List<Any>,
    @SerializedName("lottery_card")
    val lotteryCard: Any,
    @SerializedName("mode")
    val mode: Int,
    @SerializedName("notice")
    val notice: Any,
    @SerializedName("page")
    val page: Page,
    @SerializedName("replies")
    val replies: List<Reply>,
    @SerializedName("show_bvid")
    val showBvid: Boolean,
    @SerializedName("support_mode")
    val supportMode: List<Int>,
    @SerializedName("top")
    val top: Any,
    @SerializedName("upper")
    val upper: Upper,
    @SerializedName("vote")
    val vote: Int
) {
    data class Config(
        @SerializedName("read_only")
        val readOnly: Boolean,
        @SerializedName("show_del_log")
        val showDelLog: Boolean,
        @SerializedName("show_up_flag")
        val showUpFlag: Boolean,
        @SerializedName("showadmin")
        val showadmin: Int,
        @SerializedName("showentry")
        val showentry: Int,
        @SerializedName("showfloor")
        val showfloor: Int,
        @SerializedName("showtopic")
        val showtopic: Int
    )

    data class Control(
        @SerializedName("answer_guide_android_url")
        val answerGuideAndroidUrl: String,
        @SerializedName("answer_guide_icon_url")
        val answerGuideIconUrl: String,
        @SerializedName("answer_guide_ios_url")
        val answerGuideIosUrl: String,
        @SerializedName("answer_guide_text")
        val answerGuideText: String,
        @SerializedName("bg_text")
        val bgText: String,
        @SerializedName("child_input_text")
        val childInputText: String,
        @SerializedName("disable_jump_emote")
        val disableJumpEmote: Boolean,
        @SerializedName("giveup_input_text")
        val giveupInputText: String,
        @SerializedName("input_disable")
        val inputDisable: Boolean,
        @SerializedName("root_input_text")
        val rootInputText: String,
        @SerializedName("show_text")
        val showText: String,
        @SerializedName("show_type")
        val showType: Int,
        @SerializedName("web_selection")
        val webSelection: Boolean
    )

    data class Folder(
        @SerializedName("has_folded")
        val hasFolded: Boolean,
        @SerializedName("is_folded")
        val isFolded: Boolean,
        @SerializedName("rule")
        val rule: String
    )

    data class Page(
        @SerializedName("acount")
        val acount: Int,
        @SerializedName("count")
        val count: Int,
        @SerializedName("num")
        val num: Int,
        @SerializedName("size")
        val size: Int
    )

    data class Reply(
        @SerializedName("action")
        val action: Int,
        @SerializedName("assist")
        val assist: Int,
        @SerializedName("attr")
        val attr: Int,
        @SerializedName("content")
        val content: Content,
        @SerializedName("count")
        val count: Int,
        @SerializedName("ctime")
        val ctime: Long,
        @SerializedName("dialog")
        val dialog: Int,
        @SerializedName("fansgrade")
        val fansgrade: Int,
        @SerializedName("folder")
        val folder: Folder,
        @SerializedName("invisible")
        val invisible: Boolean,
        @SerializedName("like")
        val like: Int,
        @SerializedName("member")
        val member: Member,
        @SerializedName("mid")
        val mid: String,
        @SerializedName("oid")
        val oid: String,
        @SerializedName("parent")
        val parent: Int,
        @SerializedName("parent_str")
        val parentStr: String,
        @SerializedName("rcount")
        val rcount: Int,
        @SerializedName("replies")
        val replies: List<Reply>,
        @SerializedName("reply_control")
        val replyControl: ReplyControl,
        @SerializedName("root")
        val root: Int,
        @SerializedName("root_str")
        val rootStr: String,
        @SerializedName("rpid")
        val rpid: Long,
        @SerializedName("rpid_str")
        val rpidStr: String,
        @SerializedName("show_follow")
        val showFollow: Boolean,
        @SerializedName("state")
        val state: Int,
        @SerializedName("type")
        val type: Int,
        @SerializedName("up_action")
        val upAction: UpAction
    ) {
        data class Content(
            @SerializedName("device")
            val device: String,
            @SerializedName("emote")
            val emote: Map<String, Emote>? = null,
            @SerializedName("jump_url")
            val jumpUrl: JumpUrl,
            @SerializedName("max_line")
            val maxLine: Int,
            @SerializedName("members")
            val members: List<Any>,
            @SerializedName("message")
            val message: String,
            @SerializedName("plat")
            val plat: Int
        ) {
            data class Emote(
                @SerializedName("attr")
                val attr: Int,
                @SerializedName("id")
                val id: Int,
                @SerializedName("jump_title")
                val jumpTitle: String,
                @SerializedName("meta")
                val meta: Meta,
                @SerializedName("mtime")
                val mtime: Int,
                @SerializedName("package_id")
                val packageId: Int,
                @SerializedName("state")
                val state: Int,
                @SerializedName("text")
                val text: String,
                @SerializedName("type")
                val type: Int,
                @SerializedName("url")
                val url: String
            ) {
                data class Meta(
                    @SerializedName("size")
                    val size: Int
                )
            }

            class JumpUrl
        }

        data class Folder(
            @SerializedName("has_folded")
            val hasFolded: Boolean,
            @SerializedName("is_folded")
            val isFolded: Boolean,
            @SerializedName("rule")
            val rule: String
        )

        data class Member(
            @SerializedName("avatar")
            val avatar: String,
            @SerializedName("contract_desc")
            val contractDesc: String,
            @SerializedName("DisplayRank")
            val displayRank: String,
            @SerializedName("face_nft_new")
            val faceNftNew: Int,
            @SerializedName("fans_detail")
            val fansDetail: Any,
            @SerializedName("following")
            val following: Int,
            @SerializedName("is_contractor")
            val isContractor: Boolean,
            @SerializedName("is_followed")
            val isFollowed: Int,
            @SerializedName("is_senior_member")
            val isSeniorMember: Int,
            @SerializedName("level_info")
            val levelInfo: LevelInfo,
            @SerializedName("mid")
            val mid: String,
            @SerializedName("nameplate")
            val nameplate: Nameplate,
            @SerializedName("official_verify")
            val officialVerify: OfficialVerify,
            @SerializedName("pendant")
            val pendant: Pendant,
            @SerializedName("rank")
            val rank: String,
            @SerializedName("sex")
            val sex: String,
            @SerializedName("sign")
            val sign: String,
            @SerializedName("uname")
            val uname: String,
            @SerializedName("user_sailing")
            val userSailing: UserSailing,
            @SerializedName("vip")
            val vip: Vip
        ) {
            data class LevelInfo(
                @SerializedName("current_exp")
                val currentExp: Int,
                @SerializedName("current_level")
                val currentLevel: Int,
                @SerializedName("current_min")
                val currentMin: Int,
                @SerializedName("next_exp")
                val nextExp: Int
            )

            data class Nameplate(
                @SerializedName("condition")
                val condition: String,
                @SerializedName("image")
                val image: String,
                @SerializedName("image_small")
                val imageSmall: String,
                @SerializedName("level")
                val level: String,
                @SerializedName("name")
                val name: String,
                @SerializedName("nid")
                val nid: Int
            )

            data class OfficialVerify(
                @SerializedName("desc")
                val desc: String,
                @SerializedName("type")
                val type: Int
            )

            data class Pendant(
                @SerializedName("expire")
                val expire: Int,
                @SerializedName("image")
                val image: String,
                @SerializedName("image_enhance")
                val imageEnhance: String,
                @SerializedName("image_enhance_frame")
                val imageEnhanceFrame: String,
                @SerializedName("name")
                val name: String,
                @SerializedName("pid")
                val pid: Int
            )

            data class UserSailing(
                @SerializedName("cardbg")
                val cardbg: Cardbg,
                @SerializedName("cardbg_with_focus")
                val cardbgWithFocus: Any,
                @SerializedName("pendant")
                val pendant: Pendant
            ) {
                data class Cardbg(
                    @SerializedName("fan")
                    val fan: Fan,
                    @SerializedName("id")
                    val id: Long,
                    @SerializedName("image")
                    val image: String,
                    @SerializedName("jump_url")
                    val jumpUrl: String,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("type")
                    val type: String
                ) {
                    data class Fan(
                        @SerializedName("color")
                        val color: String,
                        @SerializedName("is_fan")
                        val isFan: Int,
                        @SerializedName("name")
                        val name: String,
                        @SerializedName("num_desc")
                        val numDesc: String,
                        @SerializedName("number")
                        val number: Int
                    )
                }

                data class Pendant(
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("image")
                    val image: String,
                    @SerializedName("image_enhance")
                    val imageEnhance: String,
                    @SerializedName("image_enhance_frame")
                    val imageEnhanceFrame: String,
                    @SerializedName("jump_url")
                    val jumpUrl: String,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("type")
                    val type: String
                )
            }

            data class Vip(
                @SerializedName("accessStatus")
                val accessStatus: Int,
                @SerializedName("avatar_subscript")
                val avatarSubscript: Int,
                @SerializedName("avatar_subscript_url")
                val avatarSubscriptUrl: String,
                @SerializedName("dueRemark")
                val dueRemark: String,
                @SerializedName("label")
                val label: Label,
                @SerializedName("nickname_color")
                val nicknameColor: String,
                @SerializedName("themeType")
                val themeType: Int,
                @SerializedName("vipDueDate")
                val vipDueDate: Long,
                @SerializedName("vipStatus")
                val vipStatus: Int,
                @SerializedName("vipStatusWarn")
                val vipStatusWarn: String,
                @SerializedName("vipType")
                val vipType: Int
            ) {
                data class Label(
                    @SerializedName("bg_color")
                    val bgColor: String,
                    @SerializedName("bg_style")
                    val bgStyle: Int,
                    @SerializedName("border_color")
                    val borderColor: String,
                    @SerializedName("label_theme")
                    val labelTheme: String,
                    @SerializedName("path")
                    val path: String,
                    @SerializedName("text")
                    val text: String,
                    @SerializedName("text_color")
                    val textColor: String
                )
            }
        }

        data class ReplyControl(
            @SerializedName("time_desc")
            val timeDesc: String
        )

        data class UpAction(
            @SerializedName("like")
            val like: Boolean,
            @SerializedName("reply")
            val reply: Boolean
        )
    }

    data class Upper(
        @SerializedName("mid")
        val mid: Long,
        @SerializedName("top")
        val top: Any,
        @SerializedName("vote")
        val vote: Any
    )
}