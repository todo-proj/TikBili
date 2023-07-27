package com.benyq.tikbili.ui.video

import com.benyq.tikbili.bilibili.model.RecommendVideoModel
import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState


data class VideoContainerState(
    val loading: Boolean = false
): UiState

sealed class VideoContainerEvent: UiEvent {
    data class ToastEvent(val message: String): VideoContainerEvent()
    data class VideoModelEvent(val data: List<RecommendVideoModel>, val loadMore: Boolean = false): VideoContainerEvent()
}