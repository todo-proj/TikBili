package com.benyq.tikbili.scene.shortvideo.ui.comment

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benyq.tikbili.bilibili.CommentMessageFormatter
import com.benyq.tikbili.bilibili.model.VideoReplyModel
import com.benyq.tikbili.databinding.ItemVideoCommentBinding
import com.benyq.tikbili.databinding.ItemVideoCommentChildBinding
import com.benyq.tikbili.utils.DateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter4.BaseMultiItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 *
 * @author benyq
 * @date 4/17/2024
 *
 */
class ShortVideoCommentAdapter(private val action: (VideoReplyModel.Reply)->Unit): BaseMultiItemAdapter<CommentModel>() {
    private val requestOptions = RequestOptions().circleCrop()
    private val scope = MainScope()
    private val jobs = mutableMapOf<RecyclerView.ViewHolder, Job>()
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
                    val job = scope.launch {
                        val text = withContext(Dispatchers.IO) {
                            val height = getHeight(holder.binding.tvComment.paint).toInt()
                            CommentMessageFormatter.formatMessage(
                                context,
                                reply.content.message,
                                reply.content.emote ?: emptyMap(), height
                            )
                        }
                        holder.binding.tvComment.text = text
                    }
                    jobs[holder] = job
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
                    val job = scope.launch {
                        val text = withContext(Dispatchers.IO) {
                            val height = getHeight(holder.binding.tvComment.paint).toInt()
                            CommentMessageFormatter.formatMessage(
                                context,
                                reply.content.message,
                                reply.content.emote ?: emptyMap(), height
                            )
                        }
                        holder.binding.tvComment.text = text
                    }
                    jobs[holder] = job
                }
            }

            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int,
            ): CommentChildViewHolder {
                return CommentChildViewHolder(parent)
            }

        }).onItemViewType { position, list ->
            list[position].type
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (jobs.containsKey(holder)) {
            jobs[holder]?.cancel()
            jobs.remove(holder)
        }
    }

    private fun getHeight(paint: Paint): Float {
        val fm = paint.fontMetrics
        return fm.descent - fm.ascent + fm.leading
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
