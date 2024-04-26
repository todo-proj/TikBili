package com.benyq.tikbili.player.playback.layer.base

import android.text.TextUtils
import com.benyq.tikbili.player.playback.VideoLayerHost

/**
 *
 * @author benyq
 * @date 4/26/2024
 *
 */
abstract class DialogLayer: AnimateLayer(), VideoLayerHost.BackPressedHandler {

    companion object {
        private const val REQUEST_DISMISS_REASON_DIALOG_SHOW = "request_dismiss_reason_dialog_show"
    }


    override fun onBackPressed(): Boolean {
        if (isShowing()) {
            animateDismiss()
            return true
        }else {
            return false
        }
    }

    override fun show() {
        val isShowing = isShowing()
        super.show()
        if (!isShowing && isShowing()) {
            //隐藏其他layer
            dismissLayers()
        }
    }

    protected abstract fun backPressedPriority(): Int

    override fun onBindLayerHost(layerHost: VideoLayerHost) {
        layerHost.registerBackPressedHandler(this, backPressedPriority())
    }

    override fun onUnbindLayerHost(layerHost: VideoLayerHost) {
        layerHost.unregisterBackPressedHandler(this)
    }

    private fun dismissLayers() {
        val videoLayerHost = layerHost() ?: return
        for (i in 0 until videoLayerHost.layerSize()) {
            val layer = videoLayerHost.findLayer(i)
            if (layer is AnimateLayer) {
                layer.requestAnimateDismiss(REQUEST_DISMISS_REASON_DIALOG_SHOW)
            }else if (layer is BaseLayer) {
                layer.requestDismiss(REQUEST_DISMISS_REASON_DIALOG_SHOW)
            }
        }
    }

    override fun requestDismiss(reason: String) {
        if (!TextUtils.equals(reason, REQUEST_DISMISS_REASON_DIALOG_SHOW)) {
            super.requestDismiss(reason)
        }
    }

    override fun requestHide(reason: String) {
        if (!TextUtils.equals(reason, REQUEST_DISMISS_REASON_DIALOG_SHOW)) {
            super.requestHide(reason)
        }
    }
}