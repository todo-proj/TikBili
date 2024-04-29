package com.benyq.tikbili.scene.horicontal.layer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.benyq.tikbli.player.R
import com.benyq.tikbili.player.playback.layer.base.AnimateLayer

/**
 *
 * @author benyq
 * @date 4/29/2024
 *
 */
class PlayPauseLayer: AnimateLayer() {
    override val tag: String = "PlayPauseLayer"
    private var ivPlayback: ImageView? = null

    override fun onCreateLayerView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_pasue_layer, parent, false)
        ivPlayback = view as ImageView
        view.setOnClickListener {
            val player = player() ?: return@setOnClickListener
            val isPlaying = player.isPlaying()
            changeIcon(!isPlaying)
            if (isPlaying) {
                player.pause()
            }else {
                player.start()
            }
            layerHost()?.findLayer(GestureLayer::class.java)?.showController()
        }
        return view
    }

    override fun show() {
        super.show()
        val player = player() ?: return
        changeIcon(player.isPlaying())
    }

    private fun changeIcon(isPlaying: Boolean) {
        if (isPlaying) {
            ivPlayback?.setImageResource(R.drawable.ic_pause)
        }else {
            ivPlayback?.setImageResource(R.drawable.ic_play)
        }
    }
}