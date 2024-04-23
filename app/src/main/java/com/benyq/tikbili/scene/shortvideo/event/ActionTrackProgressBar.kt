package com.benyq.tikbili.scene.shortvideo.event

import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.scene.SceneEvent

/**
 *
 * @author benyq
 * @date 4/23/2024
 *
 */
class ActionTrackProgressBar : Event(SceneEvent.Action.PROGRESS_BAR) {

    var isTracking = false
        private set

    fun init(tracking: Boolean): ActionTrackProgressBar {
        isTracking = tracking
        return this
    }

    override fun recycle() {
        super.recycle()
        isTracking = false
    }
}