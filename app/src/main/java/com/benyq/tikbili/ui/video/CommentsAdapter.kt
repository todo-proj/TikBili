package com.benyq.tikbili.ui.video

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.benyq.tikbili.R
import com.benyq.tikbili.bilibili.model.VideoReplyModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.viewholder.QuickViewHolder

/**
 *
 * @author benyq
 * @date 7/25/2023
 *
 */
class CommentsAdapter: BaseQuickAdapter<VideoReplyModel.Reply, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: VideoReplyModel.Reply?) {
        item?.let {
            holder.getView<TextView>(R.id.tv_comment).text = item.content.message
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_video_comment, parent)
    }
}