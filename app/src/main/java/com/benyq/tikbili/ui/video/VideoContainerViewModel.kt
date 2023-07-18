package com.benyq.tikbili.ui.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.bilibili.BiliRemoteRepository
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.extension.containers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
class VideoContainerViewModel(private val savedStateHandle: SavedStateHandle) : BaseViewModel() {

    val homePageContainer by containers<VideoContainerState, VideoContainerEvent>(
        VideoContainerState(false)
    )

    fun sendEvent(event: VideoContainerEvent) {
        homePageContainer.sendEvent(event)
    }

    fun loadHomeVideo() {
        request {
            repository.getRecommend()
        }.onStart {
                homePageContainer.updateState {
                    copy(loading = true)
                }
            }
            .onCompletion {
                homePageContainer.updateState {
                    copy(loading = false)
                }
            }
            .onEach {
                homePageContainer.updateState {
                    copy(data = it.item)
                }
            }
            .launchIn(viewModelScope)
    }

}