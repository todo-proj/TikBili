package com.benyq.tikbili.ui.video

import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.extension.containers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
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
    private var portraitMatrix = Matrix()

    fun queryVideoDetail(bvid: String) {
        request {
            repository.videoInfo(bvid)
        }.onEach {
            container.updateState {
                val stat = it.stat.run {
                    VideoPlayState.Stat(
                        numFormat(like),
                        numFormat(coin),
                        numFormat(reply),
                        numFormat(favorite),
                        numFormat(share)
                    )
                }
                val videoRotateMode =
                    if (it.dimension.width > it.dimension.height) VideoPlayState.VideoRotateMode.LANDSCAPE else VideoPlayState.VideoRotateMode.PORTRAIT
                copy(stat = stat, videoRotateMode = videoRotateMode, title = it.title)
            }
        }.catch {  }.onCompletion {

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
                it.getOrNull()?.let { url ->
                    container.sendEvent(VideoPlayEvent.VideoPlayUrlEvent(url))
                }
            }.catch {  }.onCompletion {
                Log.e("benyq", "queryVideoUrl onCompletion error: $it")
            }.launchIn(viewModelScope)
    }

    fun postIntent(intent: VideoPlayIntent) {
        when (intent) {
            is VideoPlayIntent.FullScreenPlayIntent -> {
                container.sendEvent(VideoPlayEvent.FullScreenPlayEvent(intent.fullScreen))
                container.updateState {
                    copy(fullScreen = intent.fullScreen)
                }
            }
            is VideoPlayIntent.LikeVideoIntent -> {

            }
            is VideoPlayIntent.RenderFirstFrameIntent -> container.updateState { copy(renderFirstFrame = true) }
            is VideoPlayIntent.ControllerVisibilityIntent -> {
                container.updateState { copy(controllerVisible = intent.visible) }
            }
        }
    }

    private fun numFormat(num: Int): String {
        if (num < 10000) return "$num"
        return "%.1f万".format((num.toFloat() / 10000))
    }

}