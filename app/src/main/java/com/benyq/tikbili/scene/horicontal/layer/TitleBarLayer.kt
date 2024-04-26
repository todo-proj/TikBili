package com.benyq.tikbili.scene.horicontal.layer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.benyq.tikbili.R
import com.benyq.tikbili.player.playback.layer.base.AnimateLayer
import com.benyq.tikbili.scene.VideoItem

/**
 *
 * @author benyq
 * @date 4/26/2024
 *
 */
class TitleBarLayer: AnimateLayer() {
    override val tag: String = "TitleLayer"

    private var ivBack: ImageView? = null
    private var tvTitle: TextView? = null

    override fun onCreateLayerView(parent: ViewGroup): View? {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_title_bar_layer, parent, false)

        ivBack = view.findViewById(R.id.back)
        tvTitle = view.findViewById(R.id.title)

        ivBack?.setOnClickListener {
            activity()?.onBackPressed()
        }

        return view
    }

    override fun show() {
        super.show()
        tvTitle?.text = resolveTitle()
    }

    private fun resolveTitle(): String {
        val source = dataSource()?: return ""
        return VideoItem.get(source)?.title ?: ""
    }
}