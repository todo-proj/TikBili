package com.benyq.tikbili.ui.video

import android.app.Application
import android.content.Context
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.bilibili.BiliRemoteRepository
import com.benyq.tikbili.player.MediaCacheFactory
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.extension.containers
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.sql.Time


/**
 *
 * @author benyq
 * @date 7/18/2023
 *
 */
class VideoPlayViewModel(private val context: Application) : BaseViewModel(context) {

    private val repository = BiliRemoteRepository()
    val container by containers<VideoPlayState, VideoPlayEvent>(VideoPlayState())
    var player: SimpleExoPlayer = SimpleExoPlayer.Builder(context).build()
    private val progressAction: Runnable

    init {
        player.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
            }
            override fun onRenderedFirstFrame() {
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                container.updateState { copy(isPlaying = isPlaying) }
            }
        })
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.playWhenReady = false

        progressAction = Runnable {
            val duration = player.duration
            val position = player.currentPosition
            val bufferedPosition = player.bufferedPosition
            container.updateState { copy(timeBar = VideoPlayState.TimeBar(duration, position, bufferedPosition)) }
        }
        viewModelScope.launch {
            while (isActive) {
                delay(500)
                progressAction.run()
            }
        }
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
                    playVideo(url, bvid)
                }
            }.catch {  }.onCompletion {
                Log.e("benyq", "queryVideoUrl onCompletion error: $it")
            }.launchIn(viewModelScope)
    }

    fun queryVideoReply(oid: String) {
        request { repository.videoReply(oid, 1) }
            .flowOn(Dispatchers.IO)
            .onEach {
                container.sendEvent(VideoPlayEvent.VideoCommentsEvent(it.replies))
            }.catch { Log.e("benyq", "queryVideoReply: $oid, ${it.message}") }.launchIn(viewModelScope)
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
            is VideoPlayIntent.CommentShowIntent -> {
                container.updateState { copy(isCommentShow = intent.visible) }
            }
        }
    }

    private fun playVideo(url: String, bvid: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setCustomCacheKey(bvid)
            .build()
        val mediaSource: ProgressiveMediaSource =
            ProgressiveMediaSource.Factory(
                MediaCacheFactory.getCacheFactory(context)
            ).createMediaSource(mediaItem)
        player.setMediaSource(mediaSource)
        player.prepare()
    }

    private fun numFormat(num: Int): String {
        if (num < 10000) return "$num"
        return "%.1f万".format((num.toFloat() / 10000))
    }

}