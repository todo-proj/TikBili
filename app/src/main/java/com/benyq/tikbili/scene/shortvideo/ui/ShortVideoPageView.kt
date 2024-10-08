package com.benyq.tikbili.scene.shortvideo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.benyq.tikbili.base.ext.findItemViewByPosition
import com.benyq.tikbili.base.ext.px
import com.benyq.tikbili.base.ext.recyclerView
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.player.VideoPlayerSettings
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.download.PreloadManager
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.player.PlayerEvent
import com.benyq.tikbili.player.playback.VideoView
import com.benyq.tikbili.scene.VideoItem
import com.benyq.tikbili.scene.widgets.load.LoadMoreAble
import com.benyq.tikbili.scene.widgets.viewpager2.OnPageChangeCallbackCompat
import com.benyq.tikbili.scene.widgets.load.impl.ViewPager2LoadMoreHelper
import com.benyq.tikbili.ui.widget.TiktokVideoLoadingView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

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
) : FrameLayout(context, attrs, defStyleAttr), DefaultLifecycleObserver, LoadMoreAble {

    private val controller: PlaybackController = PlaybackController()
    private val preloadManager = PreloadManager()

    private val viewPager: ViewPager2 = ViewPager2(context)
    private val loadingView = TiktokVideoLoadingView(context)
    private val shortVideoAdapter: ShortVideoAdapter
    private var _lifeCycle: Lifecycle? = null
    private var _interceptStartPlaybackOnResume = false
    private var _onPageChangeCallback: OnPageSelectedListener? = null
    private val viewPager2LoadMoreHelper: ViewPager2LoadMoreHelper
    private var _onLoadMoreListener: LoadMoreAble.OnLoadMoreListener? = null

    init {
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        shortVideoAdapter = ShortVideoAdapter()
        viewPager.adapter = shortVideoAdapter
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallbackCompat(viewPager) {
            override fun onPageSelected(position: Int, pager: ViewPager2?) {
                togglePlayback(position)
                _onPageChangeCallback?.onPageSelected(position)
            }
        })
        viewPager2LoadMoreHelper = ViewPager2LoadMoreHelper(viewPager) {
            _onLoadMoreListener?.onLoadMore()
        }
        viewPager.recyclerView()?.let {
            OverScrollDecoratorHelper.setUpOverScroll(it, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        }
        addView(viewPager, LayoutParams(
            LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))

        controller.addPlaybackListener(object : EventDispatcher.EventListener {
            override fun onEvent(event: Event) {
                when(event.code) {
                    PlayerEvent.State.COMPLETED -> {
                        L.d(this, "onEvent COMPLETED")
                        val player = controller.videoView()?.player() ?: return
                        if (!player.isLooping() && VideoPlayerSettings.playBackCompleteAction() == VideoPlayerSettings.PLAY_NEXT) {
                            val currentPosition: Int = viewPager.currentItem
                            L.d(this, "onEvent COMPLETED", currentPosition)
                            val nextPosition = currentPosition + 1
                            if (nextPosition < shortVideoAdapter.getItemCount()) {
                                setCurrentItem(nextPosition, true)
                            }
                        }
                    }
                }
            }
        })

        //loading
        loadingView.visibility = View.GONE
        addView(loadingView, 0, LayoutParams(40.px, 40.px, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL))
    }

    fun setOnLoadMoreListener(listener: LoadMoreAble.OnLoadMoreListener) {
        this._onLoadMoreListener = listener
    }

    fun setItems(items: List<VideoItem>) {
        preloadManager.appendItems(items.map { VideoItem.toMediaSource(it) })
        shortVideoAdapter.submitList(items)
        viewPager.getChildAt(0).post { this.play() }
    }

    fun appendItems(items: List<VideoItem>) {
        preloadManager.appendItems(items.map { VideoItem.toMediaSource(it) })
        shortVideoAdapter.addAll(items)
    }

    fun isEmpty(): Boolean {
        return shortVideoAdapter.itemCount == 0
    }

    fun play() {
        val currentPosition = viewPager.currentItem
        if (currentPosition >= 0 && shortVideoAdapter.itemCount > 0) {
            togglePlayback(currentPosition)
        }
    }

    fun setCurrentItem(position: Int, smooth: Boolean) {
        L.d(this, "setCurrentItem", position, smooth)
        viewPager.setCurrentItem(position, smooth)
    }

    private fun togglePlayback(position: Int) {
        val videoView = viewPager.findItemViewByPosition(position) as? VideoView?
        L.d(this, "togglePlayback", controller.videoView(), videoView, position)
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

    fun currentItem(): VideoItem? {
        val currentIndex = viewPager.currentItem
        return shortVideoAdapter.items.getOrNull(currentIndex)
    }

    fun setLifeCycle(lifecycle: Lifecycle) {
        if (_lifeCycle != lifecycle) {
            _lifeCycle?.removeObserver(this)
            _lifeCycle = lifecycle
        }
        _lifeCycle?.addObserver(this)
    }

    fun setOnPageChangeCallback(listener: OnPageSelectedListener) {
        _onPageChangeCallback = listener
    }

    override fun onResume(owner: LifecycleOwner) {
        if (!_interceptStartPlaybackOnResume) {
            play()
        }
        _interceptStartPlaybackOnResume = false
    }

    override fun onPause(owner: LifecycleOwner) {
        val player = controller.player()
        if (player != null && (player.isPaused() || (!player.isLooping() && player.isCompleted()))) {
            _interceptStartPlaybackOnResume = true
        } else {
            _interceptStartPlaybackOnResume = false
            controller.pausePlayback()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        _lifeCycle?.removeObserver(this)
        _lifeCycle = null
        controller.stopPlayback()
        preloadManager.cancelAll()
    }

    fun interface OnPageSelectedListener {
        fun onPageSelected(position: Int)
    }

    override fun isLoading(): Boolean {
        return viewPager2LoadMoreHelper.isLoading()
    }

    override fun finishLoadingMore() {
        loadingView.visibility = View.GONE
        viewPager2LoadMoreHelper.finishLoadingMore()
    }

    override fun startLoadingMore() {
        loadingView.visibility = View.VISIBLE
        viewPager2LoadMoreHelper.startLoadingMore()
    }
}