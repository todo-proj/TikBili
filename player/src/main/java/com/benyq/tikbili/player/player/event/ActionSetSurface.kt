package com.benyq.tikbili.player.player.event

import android.view.Surface
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.playback.PlayerEvent

/**
 *
 * @author benyq
 * @date 4/11/2024
 *
 */
class ActionSetSurface: Event(PlayerEvent.Action.SET_SURFACE) {

    var surface: Surface? = null
        private set

    fun init(surface: Surface): ActionSetSurface {
       this.surface = surface
        return this
    }

    override fun recycle() {
        super.recycle()
        surface = null
    }
}