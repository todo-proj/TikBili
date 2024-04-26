package com.benyq.tikbili.player.playback

import android.content.Context
import android.util.SparseArray
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * @author benyq
 * @date 4/7/2024
 *
 */
class VideoLayerHost(context: Context) {

    private val _layers = CopyOnWriteArrayList<VideoLayer>()
    private val _listeners = CopyOnWriteArrayList<VideoLayerHostListener>()
    private val _backHandlers = SparseArray<BackPressedHandler>()

    private val _hostView: FrameLayout
    private var _videoView: VideoView? = null

    init {
        _hostView = FrameLayout(context)
    }


    fun attachToVideoView(videoView: VideoView) {
        if (_videoView == null) {
            _videoView = videoView
            videoView.bindLayerHost(this)

            val layoutParam: FrameLayout.LayoutParams =
                _hostView.layoutParams as? FrameLayout.LayoutParams ?: FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    gravity = Gravity.CENTER
                }
            videoView.addView(_hostView, layoutParam)

            _listeners.forEach {
                it.onLayerHostAttachedToVideoView(videoView)
            }
        }
    }


    fun detachFromVideoView() {
        _videoView?.let {
            it.unbindLayerHost(this)
            it.removeView(_hostView)
            _videoView = null
            _listeners.forEach { listener->
                listener.onLayerHostAttachedToVideoView(it)
            }
        }
    }

    fun onBackPressed(): Boolean {
        for (i in _backHandlers.size() - 1 downTo 0) {
            val handler = _backHandlers.get(_backHandlers.keyAt(i))
            if (handler.onBackPressed()) {
                return true
            }
        }
        return false
    }

    fun addVideoLayerHostListener(listener: VideoLayerHostListener?) {
        if (listener != null && !_listeners.contains(listener)) {
            _listeners.add(listener)
        }
    }

    fun removeVideoLayerHostListener(listener: VideoLayerHostListener?) {
        if (listener != null) {
            _listeners.remove(listener)
        }
    }

    fun registerBackPressedHandler(backPressedHandler: BackPressedHandler, priority: Int) {
        _backHandlers[priority] = backPressedHandler
    }

    fun unregisterBackPressedHandler(backPressedHandler: BackPressedHandler) {
        for (i in _backHandlers.size() - 1 downTo 0) {
            if (_backHandlers[i] == backPressedHandler) {
                _backHandlers.remove(i)
            }
        }
    }

    fun addLayer(layer: VideoLayer) {
        if (_layers.contains(layer)) {
            return
        }
        _layers.add(layer)
        layer.bindLayerHost(this)
    }

    fun removeLayer(layer: VideoLayer) {
        if (_layers.contains(layer)) {
            removeLayerView(layer)
            layer.unBindLayerHost(this)
            _layers.remove(layer)
        }
    }

    fun removeLayer(index: Int): VideoLayer? {
        val layer = _layers.getOrNull(index)
        layer?.let { removeLayer(it) }
        return layer
    }

    fun removeLayer(tag: String): VideoLayer? {
        for (layer in _layers) {
            if (layer != null && layer.tag == tag) {
                removeLayer(layer)
                return layer
            }
        }
        return null
    }

    fun findLayer(index: Int) = _layers[index]

    fun findLayer(tag: String): VideoLayer? {
        for (layer in _layers) {
            if (layer != null && layer.tag == tag) {
                return layer
            }
        }
        return null
    }

    fun <T : VideoLayer> findLayer(layerClazz: Class<T>): T? {
        for (layer in _layers) {
            if (layer != null && layerClazz.isInstance(layer)) {
                return layer as T
            }
        }
        return null
    }

    fun indexOfLayer(layer: VideoLayer): Int {
        return _layers.indexOf(layer)
    }

    fun layerSize(): Int {
        return _layers.size
    }

    fun indexOfLayerView(layer: VideoLayer): Int {
        val layerView = layer.getView() ?: return -1
        return _hostView.indexOfChild(layerView)
    }

    fun hostView(): ViewGroup {
        return _hostView
    }

    fun videoView(): VideoView? {
        return _videoView
    }


    fun addLayerView(layer: VideoLayer) {
        val layerView = layer.getView() ?: return
        if (layerView.parent == null) {
            val layerIndex = indexOfLayer(layer)
            val layerViewIndex = calViewIndex(_hostView, layerIndex)
            _hostView.addView(layerView, layerViewIndex)
            layer.onViewAddedToHostView(_hostView)
        }
    }


    // 排除未添加到 hostView中的layerView
    private fun calViewIndex(hostView: ViewGroup, layerIndex: Int): Int {
        val preLayerIndex = layerIndex - 1
        var preLayerViewIndex = -1
        for (i in preLayerIndex downTo 0) {
            val layer = findLayer(i)
            val layerView = layer.getView()
            if (layerView != null) {
                val viewIndex = hostView.indexOfChild(layerView)
                if (viewIndex >= 0) {
                    preLayerViewIndex = viewIndex
                    break
                }
            }
        }
        return if (preLayerViewIndex < 0) 0 else preLayerViewIndex + 1
    }

    fun removeLayerView(layer: VideoLayer) {
        val layerView = layer.getView() ?: return
        val layerIndex = _layers.indexOf(layer)
        val layerViewIndex = _hostView.indexOfChild(layerView)
        if (layerIndex >= 0) {
            _hostView.removeView(layerView)
            layer.onViewRemovedFromHostView(_hostView)
        }
    }


    interface VideoLayerHostListener {
        fun onLayerHostAttachedToVideoView(videoView: VideoView)
        fun onLayerHostDetachedFromVideoView(videoView: VideoView)
    }


    fun interface BackPressedHandler {
        fun onBackPressed(): Boolean
    }

}