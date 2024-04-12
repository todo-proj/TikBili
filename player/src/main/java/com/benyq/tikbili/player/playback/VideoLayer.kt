package com.benyq.tikbili.player.playback

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.benyq.tikbili.player.player.IPlayer
import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 4/7/2024
 *
 */
abstract class VideoLayer: VideoView.VideoViewListener, VideoLayerHost.VideoLayerHostListener {

    private var _layerView: View? = null
    private var _layerHost: VideoLayerHost? = null
    abstract val tag: String

    fun bindLayerHost(layerHost: VideoLayerHost) {
        if (_layerHost == null) {
            _layerHost = layerHost
            layerHost.addVideoLayerHostListener(this)
            layerHost.videoView()?.let {
                bindVideoView(it)
            }
        }
    }

    fun unBindLayerHost(layerHost: VideoLayerHost) {
        if (_layerHost != null && _layerHost == layerHost) {
            _layerHost?.removeVideoLayerHostListener(this)
            val videoView = layerHost.videoView()
            unBindVideoView(videoView)
            _layerHost = null
        }
    }

    fun bindVideoView(videoView: VideoView?) {
        videoView?.addVideoViewListener(this)
        videoView?.controller()?.let { bindController(it) }
    }

    fun unBindVideoView(videoView: VideoView?) {
        videoView?.removeVideoViewListener(this)
        videoView?.controller()?.let { unbindController(it) }
    }

    fun bindController(playbackController: PlaybackController) {
        onBindPlaybackController(playbackController)
    }

    fun unbindController(playbackController: PlaybackController) {
        onUnbindPlaybackController(playbackController)
    }


    override fun onVideoViewBindController(controller: PlaybackController) {
        bindController(controller)
    }

    override fun onVideoViewUnbindController(controller: PlaybackController) {
        unbindController(controller)
    }

    override fun onVideoViewBindDataSource(dataSource: MediaSource) {

    }

    override fun onLayerHostAttachedToVideoView(videoView: VideoView) {
        bindVideoView(videoView)
    }

    override fun onLayerHostDetachedFromVideoView(videoView: VideoView) {
        unBindVideoView(videoView)
    }

    fun createLayerView(): View? {
        val layerHost = _layerHost ?: return null
        return createLayerView(layerHost)
    }

    fun getView(): View? {
        return _layerView
    }

    fun videoView(): VideoView? {
        return _layerHost?.videoView()
    }

    fun controller(): PlaybackController? {
        return videoView()?.controller()
    }

    fun player(): IPlayer? {
        return controller()?.player()
    }

    fun dataSource(): MediaSource? {
        return videoView()?.dataSource()
    }

    fun context(): Context? {
        return _layerView?.context ?: _layerHost?.hostView()?.context
    }

    fun activity(): FragmentActivity? {
        return context() as? FragmentActivity
    }

    open fun show() {
        if (isShowing()) return
        val layerHost = _layerHost ?: return
        val layerView = createLayerView(layerHost)
        layerHost.addLayerView(this)
        if (layerView != null && !layerView.isVisible) {
            layerView.isVisible = true
        }
    }


    open fun dismiss() {
        if (!isShowing()) return
        val layerHost = _layerHost ?: return
        layerHost.removeLayerView(this)
    }

    fun isShowing(): Boolean {
        return _layerView != null && _layerView?.isVisible == true
                && _layerHost != null && (_layerHost?.indexOfLayerView(this) ?: -1) >= 0
    }

    private fun createLayerView(layerHost: VideoLayerHost): View? {
        return _layerView ?: run {
            val layerView = onCreateLayerView(layerHost.hostView())
            _layerView = layerView
            _layerView
        }
    }

    abstract fun onCreateLayerView(parent: ViewGroup): View?

    internal fun onViewAddedToHostView(hostView: ViewGroup) {}
    internal fun onViewRemovedFromHostView(hostView: ViewGroup) {}

    protected open fun onBindPlaybackController(controller: PlaybackController?) {}
    protected open fun onUnbindPlaybackController(controller: PlaybackController?) {}
}