package com.benyq.tikbili.player.playback.layer.base

import com.benyq.tikbili.player.playback.VideoLayer

/**
 *
 * @author benyq
 * @date 4/26/2024
 *
 */
abstract class BaseLayer: VideoLayer() {
    open fun requestShow(reason: String) {
        show()
    }

    open fun requestDismiss(reason: String) {
        dismiss()
    }

    open fun requestHide(reason: String) {
        hide()
    }
}