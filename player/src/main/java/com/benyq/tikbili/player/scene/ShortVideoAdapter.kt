package com.benyq.tikbili.player.scene

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benyq.tikbili.player.helper.DisplayModeHelper
import com.benyq.tikbili.player.playback.VideoLayerHost
import com.benyq.tikbili.player.playback.VideoView
import com.benyq.tikbili.player.playback.layer.FeedRightLayer
import com.benyq.tikbili.player.playback.layer.PauseLayer
import com.benyq.tikbili.player.playback.layer.ShortVideoCoverLayer
import com.benyq.tikbili.player.playback.layer.SimpleProgressBarLayer
import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 4/10/2024
 *
 */
class ShortVideoAdapter : RecyclerView.Adapter<ShortVideoAdapter.ViewHolder>() {

    private val _items = mutableListOf<MediaSource>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<MediaSource>) {
        _items.clear()
        _items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun getItemCount() = _items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaSource = _items.getOrNull(position)
        if (mediaSource != null) {
            holder.bind(mediaSource)
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.videoView.stopPlayback()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.videoView.stopPlayback()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val videoView = createVideoView(parent.context)
                videoView.layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT
                )
                return ViewHolder(videoView)
            }
        }

        val videoView: VideoView = itemView as VideoView
        init {
            videoView.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
        }

        fun bind(mediaSource: MediaSource) {
            val currentSource = videoView.dataSource()
            if (currentSource == null) {
                videoView.bindDataSource(mediaSource)
            }else {
                if (currentSource.getUniqueId() != mediaSource.getUniqueId()) {
                    videoView.stopPlayback()
                    videoView.bindDataSource(mediaSource)
                }else {
                    // vid is same
                    if (videoView.player() == null) {
                        videoView.bindDataSource(mediaSource)
                    }
                }
            }
            videoView.setOnClickListener {
                val player = videoView.player() ?: return@setOnClickListener
                if (player.isPlaying()) {
                    videoView.pausePlayback()
                }else {
                    videoView.startPlayback()
                }
            }
        }
    }

}

private fun createVideoView(context: Context): VideoView {
    val videoView = VideoView(context)
    val layerHost = VideoLayerHost(context)
    layerHost.addLayer(ShortVideoCoverLayer())
    layerHost.addLayer(PauseLayer())
    layerHost.addLayer(FeedRightLayer())
    layerHost.addLayer(SimpleProgressBarLayer())

    layerHost.attachToVideoView(videoView)
    videoView.setupDisplayView()
    videoView.setDisplayMode(DisplayModeHelper.DISPLAY_MODE_ASPECT_FIT)
    videoView.setBackgroundColor(Color.BLACK)
    return videoView
}