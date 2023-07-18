package com.benyq.tikbili.ui.video

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.extension.containers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.net.URLDecoder


/**
 *
 * @author benyq
 * @date 7/18/2023
 *
 */
class VideoPlayViewModel : BaseViewModel() {

    val container by containers<VideoPlayState, VideoPlayEvent>(VideoPlayState())

    fun sendEvent(event: VideoPlayEvent) {
        container.sendEvent(event)
    }

    fun queryVideoDetail(bvid: String) {
        request {
            repository.videoInfo(bvid)
        }.onEach {
            container.updateState {
                val stat = it.stat.run {
                    VideoPlayState.Stat(like, coin, reply, favorite, share)
                }
                copy(stat = stat)
            }
        }.onCompletion {

        }.launchIn(viewModelScope)
    }

    fun queryVideoUrl(bvid: String, cid: String) {
        request { repository.videoUrl(bvid, cid) }
            .map {
                val durl = it.durl.first()
                kotlin.runCatching {
                    URLDecoder.decode(durl.url, "utf-8")
                }
            }
            .flowOn(Dispatchers.IO)
            .onEach {
                it.getOrNull()?.let {
                    container.updateState { copy(videoUrl = it) }
                }
            }.onCompletion {
                Log.e("benyq", "queryVideoUrl onCompletion error: $it")
            }.launchIn(viewModelScope)
    }

    fun changeFullScreen() {
        container.updateState {
            copy(fullScreen = !fullScreen)
        }
    }

}