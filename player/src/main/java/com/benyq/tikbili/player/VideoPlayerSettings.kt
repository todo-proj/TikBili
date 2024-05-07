package com.benyq.tikbili.player

import com.benyq.tikbili.base.ext.MMKVValue

/**
 *
 * @author benyq
 * @date 5/7/2024
 *
 */
object VideoPlayerSettings {

    private const val PLAYBACK_COMPLETE_ACTION = "playback_complete_action"
    const val PLAY_LOOPING = 0
    const val PLAY_NEXT = 1
    private var _playBackCompleteAction by MMKVValue(PLAYBACK_COMPLETE_ACTION, PLAY_LOOPING)

    fun playBackCompleteAction(): Int {
//        return _playBackCompleteAction
        return PLAY_NEXT
    }

    fun setPlayBackCompleteAction(action: Int) {
        if (action == PLAY_LOOPING || action == PLAY_NEXT) {
            _playBackCompleteAction = action
        }else {
            throw IllegalArgumentException("action must be PLAY_LOOPING or PLAY_NEXT")
        }
    }
}