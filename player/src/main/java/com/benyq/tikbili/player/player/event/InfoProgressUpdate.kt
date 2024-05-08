package com.benyq.tikbili.player.player.event

import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.player.PlayerEvent

/**
 *
 * @author benyq
 * @date 4/10/2024
 *
 */
class InfoProgressUpdate : Event(PlayerEvent.Info.PROGRESS_UPDATE) {

    var currentPosition: Long = 0
        private set
    var duration: Long = 0
        private set

    var buffer: Long = 0
        private set

    fun init(currentPosition: Long, duration: Long, buffer: Long): InfoProgressUpdate {
        this.duration = duration
        this.currentPosition = currentPosition
        this.buffer = buffer
        return this
    }

    override fun recycle() {
        super.recycle()
        currentPosition = 0
        duration = 0
        buffer = 0
    }
}