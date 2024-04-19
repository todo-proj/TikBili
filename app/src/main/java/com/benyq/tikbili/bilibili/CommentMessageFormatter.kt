package com.benyq.tikbili.bilibili

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.Log
import com.benyq.tikbili.bilibili.model.VideoReplyModel
import com.bumptech.glide.Glide

/**
 *
 * @author benyq
 * @date 4/17/2024
 *
 */
class CommentMessageFormatter {

    companion object {
        fun formatMessage(context: Context, message: String, emotes: Map<String, VideoReplyModel.Reply.Content.Emote>?): CharSequence {
            if (emotes == null) return message
            var newMessage: String = message
            emotes.forEach { entity->
                val key = entity.key
                val value = entity.value
                newMessage = message.replace(key, "<img src=\"${value.url}\">")
            }
            Log.d("benyq", "formatMessage: $newMessage")

            return Html.fromHtml(newMessage, { source ->
                val file = Glide.with(context).load(source).downloadOnly(40, 40).get()
                val drawable = Drawable.createFromPath(file.absolutePath)
                Log.d("benyq", "getDrawable: ${file.absolutePath}, drawable: $drawable")
                drawable
            }, null)
        }
    }

}