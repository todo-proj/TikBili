package com.benyq.tikbili.scene

/**
 *
 * @author benyq
 * @date 4/26/2024
 *
 */
class Layers {

    object BackPriority {
        private const val DIALOG_LAYER_BACK_PRIORITY = 10000
        private var sIndex = 0
        val SPEED_SELECT_DIALOG_LAYER_BACK_PRIORITY = DIALOG_LAYER_BACK_PRIORITY + sIndex++
        val VOLUME_BRIGHTNESS_DIALOG_BACK_PRIORITY = DIALOG_LAYER_BACK_PRIORITY + sIndex++
        val TIME_PROGRESS_DIALOG_LAYER_PRIORITY = DIALOG_LAYER_BACK_PRIORITY + sIndex++
        val MORE_DIALOG_LAYER_BACK_PRIORITY = DIALOG_LAYER_BACK_PRIORITY + sIndex++
    }

}