package com.benyq.tikbili.scene.shortvideo.ui.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benyq.tikbili.bilibili.model.VideoReplyModel
import com.benyq.tikbili.databinding.ItemVideoCommentBinding
import com.benyq.tikbili.databinding.ItemVideoCommentChildBinding
import com.benyq.tikbili.utils.DateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseMultiItemAdapter

/**
 *
 * @author benyq
 * @date 4/17/2024
 *
 */
class ShortVideoCommentAdapter(private val action: (VideoReplyModel.Reply)->Unit): BaseMultiItemAdapter<CommentModel>() {
    private val requestOptions = RequestOptions().circleCrop()
    init {
        addItemType(CommentModel.TYPE_MAIN, object : OnMultiItemAdapterListener<CommentModel, CommentViewHolder> {
            override fun onBind(
                holder: CommentViewHolder,
                position: Int,
                item: CommentModel?,
            ) {
                item?.reply?.let { reply->
                    val ivAvatar = holder.binding.ivAvatar
                    Glide.with(ivAvatar).load(reply.member.avatar).apply(requestOptions).into(ivAvatar)
                    holder.binding.tvNickname.text = reply.member.uname
                    holder.binding.tvCommentTime.text = DateUtils.formatTime(reply.ctime)
                    holder.binding.tvComment.text = item.formatMessage
                }
            }

            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int,
            ): CommentViewHolder {
                return CommentViewHolder(parent)
            }
        }).addItemType(CommentModel.TYPE_CHILD, object : OnMultiItemAdapterListener<CommentModel, CommentChildViewHolder> {
            override fun onBind(holder: CommentChildViewHolder, position: Int, item: CommentModel?) {
                item?.reply?.let { reply->
                    val ivAvatar = holder.binding.ivAvatar
                    Glide.with(ivAvatar).load(reply.member.avatar).apply(requestOptions).into(ivAvatar)
                    holder.binding.tvNickname.text = reply.member.uname
                    holder.binding.tvCommentTime.text = DateUtils.formatTime(reply.ctime)
                    holder.binding.tvComment.text = item.formatMessage
                }
            }

            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int,
            ): CommentChildViewHolder {
                return CommentChildViewHolder(parent)
            }

        })
            .onItemViewType { position, list ->
            list[position].type
        }
    }

}

class CommentViewHolder(val binding: ItemVideoCommentBinding): RecyclerView.ViewHolder(binding.root) {
    constructor(parent: ViewGroup): this(ItemVideoCommentBinding.inflate(
        LayoutInflater.from(parent.context), parent, false))

}

class CommentChildViewHolder(val binding: ItemVideoCommentChildBinding): RecyclerView.ViewHolder(binding.root) {
    constructor(parent: ViewGroup): this(ItemVideoCommentChildBinding.inflate(
        LayoutInflater.from(parent.context), parent, false))

}
