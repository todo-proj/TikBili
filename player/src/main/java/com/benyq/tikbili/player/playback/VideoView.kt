package com.benyq.tikbili.player.playback

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.Surface
import android.view.View
import android.widget.FrameLayout
import com.benyq.tikbili.player.helper.DisplayModeHelper
import com.benyq.tikbili.player.player.IPlayer
import com.benyq.tikbili.player.source.MediaSource
import java.util.concurrent.CopyOnWriteArrayList


/**
 *
 * @author benyq
 * @date 4/7/2024
 *
 */
class VideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), DisplayView.SurfaceListener {

    private var _controller: PlaybackController? = null
    private var _layerHost: VideoLayerHost? = null
    private var _mediaSource: MediaSource? = null
    private var _displayView: DisplayView? = null

    private val _displayModeHelper = DisplayModeHelper()
    private val _listeners = CopyOnWriteArrayList<VideoViewListener>()
    private var _interceptDispatchClick = false
    private var _hasWindowFocus = false

    init {
        setOnClickListener {
            if (!_interceptDispatchClick) {
                _listeners.forEach {
                    it.onVideoViewClick(this)
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        _displayModeHelper.apply()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (_hasWindowFocus != hasWindowFocus) {
            _hasWindowFocus = hasWindowFocus
            _listeners.forEach {
                it.onWindowFocusChanged(hasWindowFocus)
            }
        }
    }

    fun bindLayerHost(layerHost: VideoLayerHost?) {
        _layerHost = layerHost
    }

    fun unbindLayerHost(layerHost: VideoLayerHost?) {
        if (_layerHost != null && _layerHost == layerHost) {
            _layerHost = null
        }
    }

    fun bindController(controller: PlaybackController) {
        if (_controller == null) {
            _controller = controller
            _listeners.forEach {
                it.onVideoViewBindController(controller)
            }
        }
    }

    fun unbindController(controller: PlaybackController) {
        if (_controller != null && _controller == controller) {
            _controller = null
            _listeners.forEach {
                it.onVideoViewUnbindController(controller)
            }
        }
    }

    fun bindDataSource(mediaSource: MediaSource) {
        _mediaSource = mediaSource
        _listeners.forEach {
            it.onVideoViewBindDataSource(mediaSource)
        }
        if (mediaSource.displayAspectRatio > 0) {
            _displayModeHelper.setDisplayAspectRatio(mediaSource.displayAspectRatio)
        }
    }

    // TODO 暂时使用 TextureView
    fun setupDisplayView() {
        val displayView = DisplayView.create(context, DisplayView.DISPLAY_VIEW_TYPE_TEXTURE_VIEW).apply {
            addView(displayView, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
        _displayView = displayView
        _displayModeHelper.setContainerView(this)
        _displayModeHelper.setDisplayView(displayView.displayView)
        _displayView?.setSurfaceListener(this)
    }

    fun setDisplayMode(@DisplayModeHelper.DisplayMode displayMode: Int) {
        val current = getDisplayMode()
        if (displayMode != current) {
            _displayModeHelper.displayMode = displayMode
        }
    }

    fun setReuseSurface(reuseSurface: Boolean) {
        _displayView?.isReuseSurface = reuseSurface
    }

    fun addVideoViewListener(listener: VideoViewListener?) {
        if (listener != null) {
            _listeners.addIfAbsent(listener)
        }
    }

    fun removeVideoViewListener(listener: VideoViewListener?) {
        if (listener != null) {
            _listeners.remove(listener)
        }
    }

    fun setInterceptDispatchClick(intercept: Boolean) {
        _interceptDispatchClick = intercept
    }

    fun isInterceptDispatchClick() = _interceptDispatchClick

    fun startPlayback() {
        controller()?.startPlayback()
    }

    fun stopPlayback() {
        controller()?.stopPlayback()
    }

    fun pausePlayback() {
        controller()?.pausePlayback()
    }

    @DisplayModeHelper.DisplayMode
    fun getDisplayMode(): Int {
        return _displayModeHelper.displayMode
    }

    fun controller(): PlaybackController? {
        return _controller
    }

    fun dataSource(): MediaSource? {
        return _mediaSource
    }

    fun player(): IPlayer? {
        return controller()?.player()
    }

    fun surface(): Surface? {
        return _displayView?.surface
    }

    fun displayView(): View? {
        return _displayView?.displayView
    }

    interface ViewEventListener {
        fun onConfigurationChanged(newConfig: Configuration?)
        fun onWindowFocusChanged(hasWindowFocus: Boolean)
    }

    interface VideoViewListener: DisplayView.SurfaceListener, ViewEventListener {
        fun onVideoViewBindController(controller: PlaybackController) {}
        fun onVideoViewUnbindController(controller: PlaybackController) {}
        fun onVideoViewBindDataSource(dataSource: MediaSource) {}
        override fun onSurfaceAvailable(surface: Surface, width: Int, height: Int) {}
        override fun onSurfaceDestroy(surface: Surface) {}
        override fun onSurfaceSizeChanged(surface: Surface, width: Int, height: Int) {}
        override fun onSurfaceUpdated(surface: Surface) {}
        fun onVideoViewClick(videoView: VideoView) {}

        override fun onConfigurationChanged(newConfig: Configuration?) {

        }

        override fun onWindowFocusChanged(hasWindowFocus: Boolean) {

        }
    }

    override fun onSurfaceAvailable(surface: Surface, width: Int, height: Int) {
        _listeners.forEach {
            it.onSurfaceAvailable(surface, width, height)
        }
    }

    override fun onSurfaceSizeChanged(surface: Surface?, width: Int, height: Int) {
    }

    override fun onSurfaceUpdated(surface: Surface?) {
    }

    override fun onSurfaceDestroy(surface: Surface?) {
    }
}