package com.benyq.tikbili.ui.test

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.utils.StringUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.net.URLDecoder

/**
 *
 * @author benyq
 * @date 4/8/2024
 *
 */
class TestViewModel(private val application: Application) : BaseViewModel(application) {

    private val _sharedEvent = MutableSharedFlow<List<MediaSource>>()
    val sharedEvent = _sharedEvent.asSharedFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    fun getTestData() {
        viewModelScope.launch {
            request { repository.getRecommend() }
                .map { it.item }
                .take(5)
                .map {
                    val result = mutableListOf<MediaSource>()
                    it.forEach { model ->
                        kotlin.runCatching {
                            val videoDetail = repository.videoInfo(model.bvid).data
                            val data = repository.videoUrl(model.bvid, model.cid).data
                            val durl = data.durl.first()
                            result.add(
                                MediaSource(
                                    model.bvid,
                                    URLDecoder.decode(durl.url, "utf-8"),
                                    videoDetail.pic,
                                    videoDetail.dimension.width,
                                    videoDetail.dimension.height,
                                    MediaSource.Stat(
                                        StringUtils.num2String(videoDetail.stat.like),
                                        StringUtils.num2String(videoDetail.stat.coin),
                                        StringUtils.num2String(videoDetail.stat.reply),
                                        StringUtils.num2String(videoDetail.stat.favorite),
                                        StringUtils.num2String(videoDetail.stat.share)
                                    ),
                                    MediaSource.Poster(
                                        videoDetail.owner.face,
                                        videoDetail.owner.mid,
                                        videoDetail.owner.name
                                    )
                                )
                            )
                        }
                    }
                    result
                }.onStart { _loadingState.value = true }
                .onCompletion { _loadingState.value = false }
                .collect {
                    _sharedEvent.emit(it)
                }
        }
    }

}