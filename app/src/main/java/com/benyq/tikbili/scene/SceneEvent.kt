package com.benyq.tikbili.scene

/**
 *
 * @author benyq
 * @date 4/16/2024
 *
 */
interface SceneEvent {

    interface Action {
        companion object {
            const val THUMB_UP = 101
            const val COMMENT = 102
            const val COIN = 103
            const val COLLECT = 104
            const val SHARE = 105
            const val COMMENT_VISIBLE = 106
        }
    }
}