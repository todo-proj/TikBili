package com.benyq.tikbili.player.player.event

import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.player.PlayerEvent
import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 5/6/2024
 *
 */
class ActionPrepare: Event(PlayerEvent.Action.PREPARE) {

    var mediaSource: MediaSource? = null
        private set

    fun init(mediaSource: MediaSource): ActionPrepare {
        this.mediaSource = mediaSource
        return this
    }

    override fun recycle() {
        super.recycle()
        this.mediaSource = null
    }
}