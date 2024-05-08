package com.benyq.tikbili.scene.horizontal.layer

import android.media.AudioManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import com.benyq.tikbili.R
import com.benyq.tikbili.base.ext.getSystemBrightness
import com.benyq.tikbili.player.playback.layer.base.AnimateLayer
import kotlin.math.max

/**
 *
 * @author benyq
 * @date 4/28/2024
 *
 */
class BrightnessVolumeLayer: AnimateLayer() {
    override val tag: String = "BrightnessVolumeLayer"

    companion object {
        const val TYPE_VOLUME = 0
        const val TYPE_BRIGHTNESS = 1
    }

    private var ivIcon: ImageView? = null
    private var seekBar: SeekBar? = null
    private var _type: Int = TYPE_VOLUME
    private var currentResId: Int = -1

    override fun onCreateLayerView(parent: ViewGroup): View? {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_brightness_volume_layer, parent, false)

        ivIcon = view.findViewById(R.id.iv_icon)
        seekBar = view.findViewById(R.id.seek_bar)
        return view
    }


    override fun show() {
        super.show()
        if (_type == TYPE_VOLUME) {
            syncVolume()
        }else {
            syncBrightness()
        }
    }

    fun setType(type: Int) {
        _type = type
    }

    private fun syncVolume() {
        createLayerView()
        val volume = getVolumeByProgress()
        setIconResource(if (volume <= 0) R.drawable.ic_volume_mute else R.drawable.ic_volume)
        seekBar?.progress = volume
    }

    private fun syncBrightness() {
        createLayerView()
        setIconResource(R.drawable.ic_brightness)
        seekBar?.progress = getBrightnessByProgress()
    }

    fun getBrightnessByProgress(): Int {
        val activity = activity() ?: return 0
        val windowParams = activity.window?.attributes ?: return 0
        val brightness: Float =
            if (windowParams.screenBrightness == -1f) activity.getSystemBrightness() else windowParams.screenBrightness
        return max((brightness * 100).toInt(), 0)
    }

    fun setBrightnessByProgress(progress: Int) {
        val window = activity()?.window ?: return
        val params = window.attributes ?: return
        params.screenBrightness = progress / 100f
        window.attributes = params
        seekBar?.progress = progress
    }

    fun getVolumeByProgress(): Int {
        val audioManager = context()?.getSystemService(android.content.Context.AUDIO_SERVICE) as? AudioManager ?: return 50
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return volume * 100 / maxVolume
    }

    fun setVolumeByProgress(progress: Int) {
        val audioManager = context()?.getSystemService(android.content.Context.AUDIO_SERVICE) as? AudioManager ?: return
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress * maxVolume / 100, 0)

        seekBar?.progress = progress
        setIconResource(if (progress <= 0) R.drawable.ic_volume_mute else R.drawable.ic_volume)
    }

    private fun setIconResource(resId: Int) {
        if (resId == currentResId) return
        currentResId = resId
        ivIcon?.setImageResource(resId)
    }
}