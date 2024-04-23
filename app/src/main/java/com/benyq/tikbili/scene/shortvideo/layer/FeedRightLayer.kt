package com.benyq.tikbili.scene.shortvideo.layer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.benyq.tikbili.R
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlayerEvent
import com.benyq.tikbili.player.playback.VideoLayer
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbili.scene.SceneEvent
import com.benyq.tikbili.scene.shortvideo.event.ActionComment
import com.benyq.tikbili.scene.shortvideo.event.ActionCommentVisible
import com.benyq.tikbili.scene.shortvideo.event.ActionThumbUp
import com.benyq.tikbili.scene.shortvideo.event.ActionTrackProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 *
 * @author benyq
 * @date 4/12/2024
 * 右侧视频信息
 */
class FeedRightLayer : VideoLayer() {
    override val tag: String = "FeedRightLayer"

    private var tvThumbUp: TextView? = null
    private var tvReply: TextView? = null
    private var tvCoin: TextView? = null
    private var tvStar: TextView? = null
    private var tvShare: TextView? = null
    private var ivAvatar: ImageView? = null
    private var ivAddOwner: ImageView? = null

    private var llThumbUp: View? = null
    private var llReply: View? = null
    private var llCoin: View? = null
    private var llStar: View? = null
    private var llShare: View? = null

    override fun onCreateLayerView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_feed_right_layer, parent, false)
        tvThumbUp = view.findViewById(R.id.tv_thumb_up)
        llThumbUp = view.findViewById(R.id.ll_thumb_up)
        llThumbUp?.setOnClickListener {
            controller()?.dispatcher()?.obtain(ActionThumbUp::class.java)?.dispatch()
        }
        tvReply = view.findViewById(R.id.tv_reply)
        llReply = view.findViewById(R.id.ll_reply)
        llReply?.setOnClickListener {
            controller()?.dispatcher()?.obtain(ActionComment::class.java)?.dispatch()
        }
        tvCoin = view.findViewById(R.id.tv_coin)
        llCoin = view.findViewById(R.id.ll_coin)
        llCoin?.setOnClickListener {

        }
        tvStar = view.findViewById(R.id.tv_star)
        llStar = view.findViewById(R.id.ll_star)
        llStar?.setOnClickListener {

        }
        tvShare = view.findViewById(R.id.tv_share)
        llShare = view.findViewById(R.id.ll_share)
        llShare?.setOnClickListener {

        }
        ivAvatar = view.findViewById(R.id.iv_avatar)
        ivAddOwner = view.findViewById(R.id.iv_add)
        return view
    }

    override fun onBindPlaybackController(controller: PlaybackController?) {
        controller?.addPlaybackListener(listener)
    }

    override fun onUnbindPlaybackController(controller: PlaybackController?) {
        controller?.removePlaybackListener(listener)
    }

    override fun onVideoViewBindDataSource(dataSource: MediaSource) {
        show()
    }

    override fun show() {
        super.show()
        updateVideoInfo()
    }

    private fun updateVideoInfo() {
        val dataSource = dataSource() ?: return
        tvThumbUp?.text = dataSource.stat.like
        tvReply?.text = dataSource.stat.reply
        tvCoin?.text = dataSource.stat.coin
        tvStar?.text = dataSource.stat.favorite
        tvShare?.text = dataSource.stat.share
        ivAvatar?.let {
            val option = RequestOptions().centerCrop()
            Glide.with(it).load(dataSource.poster.avatar).apply(option).into(it)
        }
    }

    private val listener = object : EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when (event.code) {
                PlayerEvent.Info.DATA_SOURCE_REFRESHED -> {
                    show()
                }
                SceneEvent.Action.COMMENT_VISIBLE -> {
                    if (event.cast(ActionCommentVisible::class.java).visible) {
                        show()
                    }else {
                        hide()
                    }
                }
                SceneEvent.Action.PROGRESS_BAR -> {
                    if (event.cast(ActionTrackProgressBar::class.java).isTracking) {
                        hide()
                    }else {
                        show()
                    }
                }
            }
        }
    }
}