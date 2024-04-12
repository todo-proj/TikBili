package com.benyq.tikbili.player.player

import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 4/8/2024
 *
 */
interface IPlayerPool {

    companion object {
        val DEFAULT = DefaultPlayerPool()
    }

    fun acquire(mediaSource: MediaSource, factory: IPlayer.Factory): IPlayer

    fun get(mediaSource: MediaSource): IPlayer?

    fun recycle(player: IPlayer)
}