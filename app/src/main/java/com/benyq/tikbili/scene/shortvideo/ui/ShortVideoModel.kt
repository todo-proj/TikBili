package com.benyq.tikbili.scene.shortvideo.ui

import com.benyq.tikbili.bilibili.model.RecommendVideoModel
import com.benyq.tikbili.bilibili.model.VideoDetailModel

/**
 *
 * @author benyq
 * @date 4/17/2024
 *
 */
data class ShortVideoModel(
    val id: String,
    val video: RecommendVideoModel,
    val videoDetail: VideoDetailModel,
    val videoUrl: String
)