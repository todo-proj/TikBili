package com.benyq.tikbili.player.player.event

import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.player.PlayerEvent

/**
 *
 * @author benyq
 * @date 5/8/2024
 *
 */
class StatePlaying: Event(PlayerEvent.State.PLAYING) {

    var isPlaying = false
        private set

    fun init(isPlaying: Boolean): StatePlaying {
        this.isPlaying = isPlaying
        return this
    }

    override fun recycle() {
        super.recycle()
        isPlaying = false
    }
}