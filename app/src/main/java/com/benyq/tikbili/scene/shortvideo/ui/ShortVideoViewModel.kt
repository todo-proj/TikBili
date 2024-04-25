package com.benyq.tikbili.scene.shortvideo.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.base.ui.DataState
import com.benyq.tikbili.base.ui.PageDataModel
import com.benyq.tikbili.bilibili.BiliRemoteRepository
import com.benyq.tikbili.scene.VideoItem
import com.benyq.tikbili.scene.shortvideo.ui.comment.CommentModel
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.net.URLDecoder

/**
 *
 * @author benyq
 * @date 4/16/2024
 *
 */
class ShortVideoViewModel(app: Application) : BaseViewModel(app) {

    private val repository = BiliRemoteRepository()

    private val _videoEvent = MutableSharedFlow<DataState<List<VideoItem>>>()
    val videoEvent = _videoEvent.asSharedFlow()

    private val _commentEvent = MutableSharedFlow<DataState<PageDataModel<List<CommentModel>>>>()
    val commentEvent = _commentEvent.asSharedFlow()

    fun getRecommend() {
        flowResponse { repository.getRecommend() }
            .onEach {
                val data = it.map { videoData ->
                    val items = videoData.item.slice(0 until 5)
                    val result = mutableListOf<VideoItem>()
                    items.forEach { model ->
                        kotlin.runCatching {
                            val videoDetail = repository.videoInfo(model.bvid).getRealData()
                            val data = repository.videoUrl(model.bvid, model.cid).getRealData()
                            val durl = data.durl.first()
                            result.add(
                                VideoItem(
                                    videoDetail.bvid,
                                    videoDetail.title,
                                    videoDetail.pic,
                                    URLDecoder.decode(durl.url, "utf-8"),
                                    videoDetail.dimension.width,
                                    videoDetail.dimension.height,
                                    VideoItem.Stat(
                                        StringUtils.num2String(videoDetail.stat.like),
                                        StringUtils.num2String(videoDetail.stat.coin),
                                        StringUtils.num2String(videoDetail.stat.reply),
                                        StringUtils.num2String(videoDetail.stat.favorite),
                                        StringUtils.num2String(videoDetail.stat.share)
                                    ),
                                    VideoItem.Poster(
                                        videoDetail.owner.face,
                                        videoDetail.owner.mid,
                                        videoDetail.owner.name
                                    )
                                )
                            )
                        }
                    }
                    result.toList()
                }
                _videoEvent.emit(data)
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private var currentIndex = 1
    fun queryVideoReply(oid: String, refresh: Boolean = false) {
        if (refresh) {
            currentIndex = 1
        }
        flowResponse { repository.videoReply(oid, currentIndex) }
            .onEach {
                val data = it.map {
                    val result = mutableListOf<CommentModel>()
                    it.replies.forEach { reply ->
                        result.add(CommentModel(CommentModel.TYPE_MAIN, reply))
                        reply.replies.forEach { childReply ->
                            result.add(CommentModel(CommentModel.TYPE_CHILD, childReply))
                        }
                    }
                    currentIndex++
                    val replies = result.toList()
                    PageDataModel(replies, refresh, replies.isEmpty())
                }
                _commentEvent.emit(data)
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}