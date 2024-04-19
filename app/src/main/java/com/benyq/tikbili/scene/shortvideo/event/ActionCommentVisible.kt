package com.benyq.tikbili.scene.shortvideo.event

import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.scene.SceneEvent

/**
 *
 * @author benyq
 * @date 4/19/2024
 *
 */
class ActionCommentVisible: Event(SceneEvent.Action.COMMENT_VISIBLE){

    var visible: Boolean = false
        private set

    fun init(visible: Boolean): ActionCommentVisible {
        this.visible = visible
        return this
    }

    override fun recycle() {
        super.recycle()
    }
}