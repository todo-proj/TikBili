package com.benyq.tikbili.ui.video

import com.benyq.tikbili.bilibili.model.RecommendVideoModel
import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState


data class VideoContainerState(
    val loading: Boolean = false,
    val data: List<RecommendVideoModel> = emptyList()
): UiState

sealed class VideoContainerEvent: UiEvent {
    data class ToastEvent(val message: String): VideoContainerEvent()
}