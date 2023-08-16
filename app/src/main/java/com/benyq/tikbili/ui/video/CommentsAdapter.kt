package com.benyq.tikbili.ui.video

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Html.ImageGetter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.benyq.tikbili.R
import com.benyq.tikbili.bilibili.model.VideoReplyModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 *
 * @author benyq
 * @date 7/25/2023
 *
 */
class CommentsAdapter: BaseQuickAdapter<VideoReplyModel.Reply, QuickViewHolder>() {

    private val requestOptions = RequestOptions().circleCrop()
    lateinit var scope: CoroutineScope

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: VideoReplyModel.Reply?) {
        item?.let {
            val ivAvatar = holder.getView<ImageView>(R.id.iv_avatar)
            Glide.with(ivAvatar).load(item.member.avatar).apply(requestOptions).into(ivAvatar)
            holder.getView<TextView>(R.id.tv_nickname).text = item.member.uname
            holder.getView<TextView>(R.id.tv_comment_time).text = formatTime(item.ctime)
            holder.getView<TextView>(R.id.tv_comment).text = formatMessage(item.content.message, item.content.emote)
            scope.launch {
                val str = withContext(Dispatchers.IO) {
                    formatMessage(item.content.message, item.content.emote)
                }
                holder.getView<TextView>(R.id.tv_comment).text = str
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_video_comment, parent)
    }

    private fun formatMessage(message: String, emotes: Map<String, VideoReplyModel.Reply.Content.Emote>?): CharSequence {
        if (emotes == null) return message
        var newMessage: String = message
        emotes.forEach { entity->
            val key = entity.key
            val value = entity.value
            newMessage = message.replace(key, "<img src=\"${value.url}\">")
        }
        Log.d("benyq", "formatMessage: $newMessage")

        return Html.fromHtml(newMessage, object: ImageGetter{
            override fun getDrawable(source: String?): Drawable? {
                val file = Glide.with(context).load(source).downloadOnly(40, 40).get()
                val drawable = Drawable.createFromPath(file.absolutePath)
                Log.d("benyq", "getDrawable: ${file.absolutePath}, drawable: $drawable")
                return drawable
            }
        }, null)
    }

    private fun formatTime(cTime: Long): String {
        val calender = Calendar.getInstance()
        calender.timeInMillis = cTime * 1000
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH) + 1
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        return ""
    }
}