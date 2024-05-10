package com.benyq.tikbili.scene.shortvideo.ui.comment

import android.content.Context
import android.text.Spanned
import com.benyq.tikbili.bilibili.CommentMessageFormatter
import com.benyq.tikbili.bilibili.model.VideoReplyModel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

/**
 *
 * @author benyq
 * @date 4/17/2024
 *
 */
data class CommentModel(
    val type: Int,
    val reply: VideoReplyModel.Reply
) {
    companion object {
        const val TYPE_MAIN = 0
        const val TYPE_CHILD = 1
        const val TYPE_EXPAND = 2
    }

    var formatMessage: Spanned? = null
}