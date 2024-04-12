package com.benyq.tikbili.player.playback.layer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlayerEvent
import com.benyq.tikbili.player.playback.VideoLayer
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbli.player.R
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

    private var tvLike: TextView? = null
    private var tvReply: TextView? = null
    private var tvCoin: TextView? = null
    private var tvStar: TextView? = null
    private var tvShare: TextView? = null
    private var ivAvatar: ImageView? = null
    private var ivAddOwner: ImageView? = null

    override fun onCreateLayerView(parent: ViewGroup): View? {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vide_feed_right_layer, parent, false)
        tvLike = view.findViewById(R.id.tv_like)
        tvReply = view.findViewById(R.id.tv_reply)
        tvCoin = view.findViewById(R.id.tv_coin)
        tvStar = view.findViewById(R.id.tv_star)
        tvShare = view.findViewById(R.id.tv_share)
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
        tvLike?.text = dataSource.stat.like
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
            }
        }
    }
}