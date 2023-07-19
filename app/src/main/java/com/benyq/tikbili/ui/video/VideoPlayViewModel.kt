package com.benyq.tikbili.ui.video

import android.graphics.Matrix
import android.graphics.RectF
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.player.ExoVideoPlayer
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.extension.containers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.net.URLDecoder
import kotlin.math.min


/**
 *
 * @author benyq
 * @date 7/18/2023
 *
 */
class VideoPlayViewModel : BaseViewModel() {

    val container by containers<VideoPlayState, VideoPlayEvent>(VideoPlayState())
    private var portraitMatrix = Matrix()

    fun sendEvent(event: VideoPlayEvent) {
        container.sendEvent(event)
    }

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
                copy(stat = stat, videoRotateMode = videoRotateMode)
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

    fun postIntent(intent: VideoPlayIntent) {
        when (intent) {
            is VideoPlayIntent.FillScreenPlayIntent -> changeFullScreen()
            is VideoPlayIntent.PlayPauseVideoIntent -> {
                container.updateState { copy(isPlaying = intent.play) }
            }
            is VideoPlayIntent.LikeVideoIntent -> {

            }

            is VideoPlayIntent.ResizePlayViewIntent -> {

                val textureViewWidth = intent.viewSize.width.toFloat()
                val textureViewHeight = intent.viewSize.height.toFloat()
                val matrix = Matrix()
                //第1步:把视频区移动到View区,使两者中心点重合.
                matrix.preTranslate(
                    (textureViewWidth - intent.videoSize.width) / 2,
                    (textureViewHeight - intent.videoSize.height) / 2
                )
                //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
                matrix.preScale(
                    intent.videoSize.width / textureViewWidth,
                    intent.videoSize.height / textureViewHeight
                )
                //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
                val sx = textureViewWidth / intent.videoSize.width
                val sy = textureViewHeight / intent.videoSize.height
                val scale = min(sx, sy)
                matrix.postScale(scale, scale, textureViewWidth / 2, textureViewHeight / 2)
                portraitMatrix = matrix
                container.updateState {
                    copy(videoMatrix = matrix)
                }
            }
        }
    }

    private fun changeFullScreen() {
        container.updateState {
            val newFullScreen = !fullScreen
            copy(fullScreen = newFullScreen, videoMatrix = if (newFullScreen) Matrix() else portraitMatrix)
        }
    }

    private fun numFormat(num: Int): String {
        if (num < 10000) return "$num"
        return "%.1f万".format((num.toFloat() / 10000))
    }

}