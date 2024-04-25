package com.benyq.tikbili.scene.shortvideo.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benyq.tikbili.player.helper.DisplayModeHelper
import com.benyq.tikbili.player.playback.VideoLayerHost
import com.benyq.tikbili.player.playback.VideoView
import com.benyq.tikbili.scene.shortvideo.layer.FeedRightLayer
import com.benyq.tikbili.player.playback.layer.PauseLayer
import com.benyq.tikbili.scene.shortvideo.layer.ShortVideoCoverLayer
import com.benyq.tikbili.scene.VideoItem
import com.benyq.tikbili.scene.shortvideo.layer.FullScreenLayer
import com.benyq.tikbili.scene.shortvideo.layer.ShortVideoProgressBarLayer

/**
 *
 * @author benyq
 * @date 4/10/2024
 *
 */
class ShortVideoAdapter : RecyclerView.Adapter<ShortVideoAdapter.ViewHolder>() {

    private val _items = mutableListOf<VideoItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<VideoItem>) {
        _items.clear()
        _items.addAll(items)
        notifyDataSetChanged()
    }

    fun items() = _items

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

            private fun createVideoView(context: Context): VideoView {
                val videoView = VideoView(context)
                val layerHost = VideoLayerHost(context)
                layerHost.addLayer(ShortVideoCoverLayer())
                layerHost.addLayer(PauseLayer())
                layerHost.addLayer(FullScreenLayer())
                layerHost.addLayer(FeedRightLayer())
                layerHost.addLayer(ShortVideoProgressBarLayer())

                layerHost.attachToVideoView(videoView)
                videoView.setupDisplayView()
                videoView.setDisplayMode(DisplayModeHelper.DISPLAY_MODE_ASPECT_FIT)
                videoView.setBackgroundColor(Color.BLACK)
                videoView.transitionName = "video"
                return videoView
            }
        }

        val videoView: VideoView = itemView as VideoView
        init {
            videoView.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
        }

        fun bind(model: VideoItem) {
            val mediaSource = VideoItem.toMediaSource(model)
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
        }
    }

}