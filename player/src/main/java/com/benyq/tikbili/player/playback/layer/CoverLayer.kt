package com.benyq.tikbili.player.playback.layer

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.helper.DisplayModeHelper
import com.benyq.tikbili.player.helper.DisplayModeHelper.calDisplayAspectRatio
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.PlaybackEvent
import com.benyq.tikbili.player.player.PlayerEvent
import com.benyq.tikbili.player.playback.layer.base.BaseLayer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 *
 * @author benyq
 * @date 4/11/2024
 *
 */
open class CoverLayer : BaseLayer() {
    override val tag: String = "CoverLayer"

    private val _displayModeHelper: DisplayModeHelper = DisplayModeHelper()

    override fun onCreateLayerView(parent: ViewGroup): View {
        val imageView = ImageView(parent.context)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setBackgroundColor(Color.BLACK)
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        layoutParams.gravity = Gravity.CENTER
        imageView.layoutParams = layoutParams
        _displayModeHelper.setDisplayView(imageView)
        _displayModeHelper.setContainerView(parent as FrameLayout)
        return imageView
    }

    override fun onBindPlaybackController(controller: PlaybackController?) {
        controller?.addPlaybackListener(listener)
    }

    override fun onUnbindPlaybackController(controller: PlaybackController?) {
        controller?.removePlaybackListener(listener)
    }

    override fun show() {
        super.show()
        load()
    }

    protected open fun load() {
        val imageView = getView() as? ImageView ?: return
        val coverUrl = resolveCoverUrl() ?: return
        val activity = activity() ?: return
        if (!activity.isDestroyed) {
            Glide.with(imageView.context).load(coverUrl).listener(glideListener).into(imageView)
        }
    }

    private fun resolveCoverUrl(): String? {
        return dataSource()?.coverUrl
    }

    protected val glideListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean,
        ) = false

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean,
        ): Boolean {
            _displayModeHelper.setDisplayAspectRatio(
                calDisplayAspectRatio(
                    resource.intrinsicWidth,
                    resource.intrinsicHeight,
                    1f
                )
            )
            videoView()?.let {
                _displayModeHelper.displayMode = it.getDisplayMode()
            }
            return false
        }

    }

    private val listener = object : EventDispatcher.EventListener {
        override fun onEvent(event: Event) {
            when (event.code) {
                PlaybackEvent.Action.START_PLAYBACK -> {
                    val player = player() ?: return
                    if (player.isInPlaybackState()) {
                        return
                    }
                    show()
                }
                PlaybackEvent.Action.STOP_PLAYBACK -> show()
                PlayerEvent.Action.SET_SURFACE -> {
                    val player = player() ?: return
                    if (player.isInPlaybackState()) {
                        dismiss()
                    } else {
                        show()
                    }
                }
                PlayerEvent.Action.START -> {
                    val player = player() ?: return
                    if (player.isPaused()) {
                        dismiss()
                    }
                }
                PlayerEvent.State.STOPPED, PlayerEvent.State.RELEASED -> {
                    show()
                }
                PlayerEvent.Info.VIDEO_RENDERING_START -> {
                    dismiss()
                }
            }
        }

    }
}