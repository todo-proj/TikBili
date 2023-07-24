package com.benyq.tikbili.ui.video

import android.graphics.Matrix
import android.util.Size
import com.benyq.tikbili.bilibili.model.VideoDetailModel
import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState

/**
 *
 * @author benyq
 * @date 7/18/2023
 *
 */

data class VideoPlayState(
    val videoRotateMode: VideoRotateMode = VideoRotateMode.PORTRAIT,
    //是否全屏播放
    val fullScreen: Boolean = false,
    val title: String = "",
    val stat: Stat = Stat(),
    val relatedVideos: List<VideoDetailModel.UgcSeason.Section.Episode> = emptyList()
): UiState {
    enum class VideoRotateMode {
        PORTRAIT, LANDSCAPE
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
}


sealed class VideoPlayEvent: UiEvent {
    data class VideoPlayUrlEvent(val videoUrl: String = ""): VideoPlayEvent()
}

sealed class VideoPlayIntent {
    object FillScreenPlayIntent : VideoPlayIntent()
    data class LikeVideoIntent(val bvid: String): VideoPlayIntent()
}