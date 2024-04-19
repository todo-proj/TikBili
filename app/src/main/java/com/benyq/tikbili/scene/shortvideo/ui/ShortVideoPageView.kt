package com.benyq.tikbili.scene.shortvideo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.benyq.tikbili.base.ext.findItemViewByPosition
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlaybackEvent
import com.benyq.tikbili.player.playback.PlayerEvent
import com.benyq.tikbili.player.playback.VideoView
import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 4/10/2024
 *
 */
class ShortVideoPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val controller: PlaybackController = PlaybackController()

    private val viewPager: ViewPager2
    private val shortVideoAdapter: ShortVideoAdapter

    init {
        viewPager = ViewPager2(context)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        shortVideoAdapter = ShortVideoAdapter()
        viewPager.adapter = shortVideoAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                togglePlayback(position)
            }
        })
        addView(viewPager, LayoutParams(
            LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))

        controller.addPlaybackListener(object : EventDispatcher.EventListener {
            override fun onEvent(event: Event) {
                when(event.code) {
                    PlayerEvent.State.COMPLETED -> {

                    }
                }
            }
        })
    }

    fun setItems(items: List<ShortVideoModel>) {
        shortVideoAdapter.setItems(items)
        viewPager.getChildAt(0).post { this.play() }
    }

    fun play() {
        val currentPosition = viewPager.currentItem
        if (currentPosition >= 0 && shortVideoAdapter.itemCount > 0) {
            togglePlayback(currentPosition)
        }
    }

    private fun togglePlayback(position: Int) {
        val videoView = viewPager.findItemViewByPosition(position) as? VideoView?
        if (controller.videoView() == null) {
            if (videoView != null) {
                controller.bind(videoView)
                controller.startPlayback()
            }
        }else {
            if (videoView != null && controller.videoView() != videoView) {
                controller.videoView()?.stopPlayback()
                controller.bind(videoView)
            }
            controller.startPlayback()
        }
    }

    fun controller(): PlaybackController {
        return controller
    }

    fun currentItem(): ShortVideoModel? {
        val currentIndex = viewPager.currentItem
        return shortVideoAdapter.items().getOrNull(currentIndex)
    }
}