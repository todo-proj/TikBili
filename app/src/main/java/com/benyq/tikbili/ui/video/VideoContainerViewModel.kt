package com.benyq.tikbili.ui.video

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.bilibili.BiliRemoteRepository
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.extension.containers
import kotlinx.coroutines.flow.catch
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
class VideoContainerViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : BaseViewModel(application) {

    private val repository = BiliRemoteRepository()

    val container by containers<VideoContainerState, VideoContainerEvent>(
        VideoContainerState(false)
    )

    fun loadHomeVideo(loadMore: Boolean = false) {
        request {
            repository.getRecommend(5)
        }.onStart {
            container.updateState {
                    copy(loading = !loadMore)
                }
            }
            .onCompletion {
                container.updateState {
                    copy(loading = false)
                }
            }
            .onEach {
                container.sendEvent(VideoContainerEvent.VideoModelEvent(it.item, loadMore))
            }.catch {

            }
            .launchIn(viewModelScope)
    }

}