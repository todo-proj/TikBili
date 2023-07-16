package com.benyq.tikbili.ui.video

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.extension.containers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
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

    private val repository = HomeVideoRepository()
    val homePageContainer by containers<VideoContainerState, VideoContainerEvent>(
        VideoContainerState(false)
    )

    fun sendEvent(event: VideoContainerEvent) {
        homePageContainer.sendEvent(event)
    }

    fun loadHomeVideo() {
        repository.loadHomeVideo()
            .onStart {
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
                it.onSuccess { data ->
                    homePageContainer.updateState {
                        copy(data = data)
                    }
                }.onFailure {
                    sendEvent(VideoContainerEvent.ToastEvent("视频数据拉取失败"))
                }
            }
            .launchIn(viewModelScope)
    }

}