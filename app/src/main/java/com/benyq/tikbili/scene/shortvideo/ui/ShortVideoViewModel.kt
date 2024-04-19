package com.benyq.tikbili.scene.shortvideo.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.base.ui.DataState
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.bilibili.BiliRemoteRepository
import com.benyq.tikbili.scene.shortvideo.ui.comment.CommentModel
import com.benyq.tikbili.ui.base.BaseViewModel
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

    private val _videoEvent = MutableSharedFlow<DataState<List<ShortVideoModel>>>()
    val videoEvent = _videoEvent.asSharedFlow()

    private val _commentEvent = MutableSharedFlow<DataState<List<CommentModel>>>()
    val commentEvent = _commentEvent.asSharedFlow()

    fun getRecommend() {
        flowResponse { repository.getRecommend() }
            .onEach {
                val data = it.map { videoData ->
                    val items = videoData.item.slice(0 until 5)
                    val result = mutableListOf<ShortVideoModel>()
                    items.forEach { model ->
                        kotlin.runCatching {
                            val videoDetail = repository.videoInfo(model.bvid).getRealData()
                            val data = repository.videoUrl(model.bvid, model.cid).getRealData()
                            val durl = data.durl.first()
                            result.add(
                                ShortVideoModel(
                                    model.id,
                                    model,
                                    videoDetail,
                                    URLDecoder.decode(durl.url, "utf-8")
                                )
                            )
                        }
                    }
                    result.toList()
                }
                _videoEvent.emit(data)
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun queryVideoReply(oid: String) {
        flowResponse { repository.videoReply(oid, 1) }
            .onEach {
                val data = it.map {
                    val result = mutableListOf<CommentModel>()
                    it.replies.forEach { reply ->
                        result.add(CommentModel(CommentModel.TYPE_MAIN, reply))
                        reply.replies.forEach { childReply ->
                            result.add(CommentModel(CommentModel.TYPE_CHILD, childReply))
                        }
                    }
                    L.d(this@ShortVideoViewModel, "queryVideoReply", "prepare tasks")
                    result.forEach { model ->
                        model.formatMessage(getApplication())
                    }
                    L.d(this@ShortVideoViewModel, "queryVideoReply", "done tasks")
                    result.toList().apply {
                        L.d(this@ShortVideoViewModel, "queryVideoReply", "return result")
                    }
                }
                _commentEvent.emit(data)
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}